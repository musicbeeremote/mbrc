package com.kelsos.mbrc.core.networking.client

import com.kelsos.mbrc.core.networking.protocol.Clock
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import timber.log.Timber

/**
 * Holds commands that could not be written to the socket so they can be replayed once the
 * connection is restored.
 *
 * Three rules keep a replay from surprising the user:
 * - **Bounded lifetime.** A command older than [ttlMs] is dropped instead of replayed. The age is
 *   measured from the moment the user issued it, so a command that keeps failing on a flapping
 *   connection eventually ages out instead of being refreshed forever.
 * - **Collapsing of value-setting commands.** Contexts in [LAST_WRITE_WINS] describe a value rather
 *   than an event, so only the newest matters: a volume drag replays the final level, and repeated
 *   play/pause taps replay as a single toggle. Event commands such as `next` or a queue removal are
 *   never collapsed, because two of them mean the user asked for two things.
 * - **Protocol chatter is never buffered.** A stale pong or handshake message is meaningless on a
 *   new socket, and the handshake is re-driven by the connection itself.
 *
 * Buffering a command is silent: the connection indicator already tells the user they are offline,
 * and the command is still expected to happen. Discarding one is not, because that is the only
 * point at which a tap is lost with nothing else to signal it, so [onDiscarded] is invoked with the
 * number of commands given up on.
 */
class PendingCommandBuffer(
  private val clock: Clock,
  private val ttlMs: Long = DEFAULT_TTL_MS,
  private val capacity: Int = DEFAULT_CAPACITY,
  private val notifyIntervalMs: Long = DEFAULT_NOTIFY_INTERVAL_MS,
  private val onDiscarded: (count: Int) -> Unit = {}
) {
  private data class Pending(val message: SocketMessage, val queuedAt: Long)

  private val pending = ArrayDeque<Pending>()

  private var discardedSinceNotify = 0
  private var lastNotifiedAt: Long? = null

  /**
   * Buffers [message] for replay. Returns false when the message is protocol chatter that must not
   * survive the connection it was queued for.
   */
  fun stash(message: SocketMessage): Boolean {
    val accepted = stashLocked(message)
    reportDiscarded()
    return accepted
  }

  /**
   * Returns the commands that are still fresh enough to replay, oldest first, and empties the
   * buffer.
   */
  fun drain(): List<SocketMessage> {
    val replayable = drainLocked()
    reportDiscarded()
    return replayable
  }

  @Synchronized
  fun clear() {
    pending.clear()
    // An explicit disconnect is the user's own doing, so anything still buffered is not a loss
    // worth reporting.
    discardedSinceNotify = 0
  }

  @Synchronized
  fun size(): Int = pending.size

  @Synchronized
  private fun stashLocked(message: SocketMessage): Boolean {
    if (!isReplayable(message)) {
      return false
    }

    val now = clock.now()
    dropExpired(now)

    if (collapseInPlace(message, now)) {
      return true
    }

    pending.addLast(Pending(message, now))
    while (pending.size > capacity) {
      val dropped = pending.removeFirst()
      Timber.d("Pending command buffer full, dropping ${dropped.message}")
      discardedSinceNotify++
    }
    return true
  }

  @Synchronized
  private fun drainLocked(): List<SocketMessage> {
    dropExpired(clock.now())
    val replayable = pending.map { it.message }
    pending.clear()
    return replayable
  }

  /**
   * Replaces an earlier command for the same value-setting context, keeping its position in the
   * queue and its original age. Returns false when [message] is not a collapsible command, or when
   * there is nothing to collapse it into.
   */
  private fun collapseInPlace(message: SocketMessage, now: Long): Boolean {
    if (message.context !in LAST_WRITE_WINS) {
      return false
    }
    val index = pending.indexOfFirst { it.message.context == message.context }
    if (index == -1) {
      return false
    }
    // A command that keeps failing must age from when the user issued it, not from the last retry,
    // otherwise it can be replayed indefinitely on a flapping connection.
    val queuedAt = if (pending[index].message == message) pending[index].queuedAt else now
    pending[index] = Pending(message, queuedAt)
    return true
  }

  private fun dropExpired(now: Long) {
    pending.removeAll { entry ->
      val expired = now - entry.queuedAt >= ttlMs
      if (expired) {
        Timber.d("Dropping stale pending command ${entry.message}")
        discardedSinceNotify++
      }
      expired
    }
  }

  /**
   * Hands the accumulated discard count to [onDiscarded], outside the lock so the callback cannot
   * deadlock against the buffer.
   *
   * Discards arrive in bursts (a whole batch ages out at once) but can also trickle in, one per tap,
   * once a full buffer starts overflowing. Reporting at most once per [notifyIntervalMs] keeps that
   * from turning into a stream of snackbars; a suppressed count is not lost, it is added to the next
   * report.
   */
  private fun reportDiscarded() {
    val count = takeDiscardReport()
    if (count > 0) {
      onDiscarded(count)
    }
  }

  @Synchronized
  private fun takeDiscardReport(): Int {
    if (discardedSinceNotify == 0) {
      return 0
    }
    val now = clock.now()
    val suppressed = lastNotifiedAt?.let { now - it < notifyIntervalMs } == true
    if (suppressed) {
      return 0
    }
    val count = discardedSinceNotify
    discardedSinceNotify = 0
    lastNotifiedAt = now
    return count
  }

  private fun isReplayable(message: SocketMessage): Boolean = message.context !in NOT_REPLAYABLE

  companion object {
    /**
     * A dropped command is worth replaying only while the user still expects it to happen, but the
     * window has to outlast a reconnect or nothing is ever replayed: the service waits
     * `ServiceLifecycleManager.RECONNECTION_DELAY_MS` (15s) before reconnecting, and the connection
     * manager adds its own start delay on top of that.
     */
    const val DEFAULT_TTL_MS = 45_000L
    const val DEFAULT_CAPACITY = 16

    /**
     * Long enough that an overflowing buffer cannot produce a snackbar per tap, short enough that a
     * second round of losses after a reconnect still reads as news.
     */
    const val DEFAULT_NOTIFY_INTERVAL_MS = 10_000L

    /**
     * Handshake and keep-alive traffic belongs to the socket it was queued for.
     */
    private val NOT_REPLAYABLE = setOf(
      Protocol.Ping.context,
      Protocol.Pong.context,
      Protocol.Player.context,
      Protocol.ProtocolTag.context,
      Protocol.Init.context
    )

    /**
     * Commands that set a value rather than describe an event: only the newest matters.
     */
    private val LAST_WRITE_WINS = setOf(
      Protocol.PlayerVolume.context,
      Protocol.PlayerMute.context,
      Protocol.PlayerRepeat.context,
      Protocol.PlayerShuffle.context,
      Protocol.PlayerState.context,
      Protocol.PlayerPlayPause.context,
      Protocol.NowPlayingPosition.context,
      Protocol.NowPlayingRating.context
    )
  }
}
