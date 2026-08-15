package com.kelsos.mbrc.core.networking.client

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.networking.protocol.Clock
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import org.junit.Test

class PendingCommandBufferTest {
  private var now = 0L
  private val clock = Clock { now }

  private val discarded = mutableListOf<Int>()

  private fun buffer(
    ttlMs: Long = PendingCommandBuffer.DEFAULT_TTL_MS,
    capacity: Int = PendingCommandBuffer.DEFAULT_CAPACITY
  ) = PendingCommandBuffer(clock, ttlMs, capacity) { discarded.add(it) }

  @Test
  fun `buffers a failed command for replay`() {
    val buffer = buffer()
    val playPause = SocketMessage.create(Protocol.PlayerPlayPause)

    assertThat(buffer.stash(playPause)).isTrue()

    assertThat(buffer.drain()).containsExactly(playPause)
  }

  @Test
  fun `draining empties the buffer`() {
    val buffer = buffer()
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))

    buffer.drain()

    assertThat(buffer.drain()).isEmpty()
  }

  @Test
  fun `repeated taps on the same control collapse to a single command`() {
    val buffer = buffer()
    repeat(3) { buffer.stash(SocketMessage.create(Protocol.PlayerPlayPause)) }

    assertThat(buffer.drain()).containsExactly(SocketMessage.create(Protocol.PlayerPlayPause))
  }

  @Test
  fun `a volume drag replays only the final value`() {
    val buffer = buffer()
    buffer.stash(SocketMessage.create(Protocol.PlayerVolume, 20))
    buffer.stash(SocketMessage.create(Protocol.PlayerVolume, 35))
    buffer.stash(SocketMessage.create(Protocol.PlayerVolume, 50))

    assertThat(buffer.drain()).containsExactly(SocketMessage.create(Protocol.PlayerVolume, 50))
  }

  @Test
  fun `the ttl outlasts a reconnect cycle`() {
    // Otherwise nothing is ever replayed: the service waits 15s before reconnecting and the
    // connection manager adds its own start delay on top of that.
    assertThat(PendingCommandBuffer.DEFAULT_TTL_MS).isGreaterThan(20_000L)
  }

  @Test
  fun `distinct commands are all kept in order`() {
    val buffer = buffer()
    val next = SocketMessage.create(Protocol.PlayerNext)
    val volume = SocketMessage.create(Protocol.PlayerVolume, 40)
    buffer.stash(next)
    buffer.stash(volume)

    assertThat(buffer.drain()).containsExactly(next, volume).inOrder()
  }

  @Test
  fun `event commands are never collapsed`() {
    val buffer = buffer()
    val next = SocketMessage.create(Protocol.PlayerNext)

    // Two taps on next mean the user wants to skip two tracks.
    buffer.stash(next)
    buffer.stash(next)

    assertThat(buffer.drain()).containsExactly(next, next)
  }

  @Test
  fun `commands scoped to a queue position are never replayed`() {
    // The queue can move on while the buffer waits, so a removal queued at index 1 would delete
    // whatever took that slot, and a rating would land on the next song.
    val buffer = buffer()

    assertThat(buffer.stash(SocketMessage.create(Protocol.NowPlayingListRemove, 1))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.NowPlayingListMove, 1))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, 1))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.NowPlayingRating, 4))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.NowPlayingPosition, 1000))).isFalse()

    assertThat(buffer.drain()).isEmpty()
  }

  @Test
  fun `a command is only replayed if it is on the allow list`() {
    // Anything not explicitly cleared for replay stays out, so a protocol context added later is
    // not silently replayable.
    val buffer = buffer()

    assertThat(buffer.stash(SocketMessage.create(Protocol.PlayerScrobble, true))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.PlaylistPlay, "a"))).isFalse()
  }

  @Test
  fun `collapsing keeps the position of the command it replaces`() {
    val buffer = buffer()
    val quietVolume = SocketMessage.create(Protocol.PlayerVolume, 10)
    val loudVolume = SocketMessage.create(Protocol.PlayerVolume, 80)
    val next = SocketMessage.create(Protocol.PlayerNext)

    buffer.stash(quietVolume)
    buffer.stash(next)
    buffer.stash(loudVolume)

    assertThat(buffer.drain()).containsExactly(loudVolume, next).inOrder()
  }

  @Test
  fun `a command that keeps failing ages from when the user issued it`() {
    val buffer = buffer(ttlMs = 10_000L)
    val playPause = SocketMessage.create(Protocol.PlayerPlayPause)
    buffer.stash(playPause)

    // Re-stashed by a failing retry, which must not refresh its age.
    now += 6_000L
    buffer.stash(playPause)
    now += 6_000L

    assertThat(buffer.drain()).isEmpty()
  }

  @Test
  fun `commands older than the ttl are dropped instead of replayed`() {
    val buffer = buffer(ttlMs = 10_000L)
    buffer.stash(SocketMessage.create(Protocol.PlayerPlayPause))

    now += 10_000L

    assertThat(buffer.drain()).isEmpty()
  }

  @Test
  fun `fresh commands survive while stale ones are dropped`() {
    val buffer = buffer(ttlMs = 10_000L)
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))

    now += 9_000L
    val volume = SocketMessage.create(Protocol.PlayerVolume, 30)
    buffer.stash(volume)

    now += 2_000L

    assertThat(buffer.drain()).containsExactly(volume)
  }

  @Test
  fun `protocol chatter is never buffered`() {
    val buffer = buffer()

    assertThat(buffer.stash(SocketMessage.create(Protocol.Pong))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.Ping))).isFalse()
    assertThat(buffer.stash(SocketMessage.player())).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.Init))).isFalse()
    assertThat(buffer.stash(SocketMessage.create(Protocol.ProtocolTag))).isFalse()

    assertThat(buffer.drain()).isEmpty()
  }

  @Test
  fun `the oldest command is dropped once capacity is reached`() {
    val buffer = buffer(capacity = 2)
    val next = SocketMessage.create(Protocol.PlayerNext)
    val previous = SocketMessage.create(Protocol.PlayerPrevious)

    buffer.stash(next)
    buffer.stash(previous)
    buffer.stash(next)

    assertThat(buffer.drain()).containsExactly(previous, next).inOrder()
  }

  @Test
  fun `clear discards everything`() {
    val buffer = buffer()
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))

    buffer.clear()

    assertThat(buffer.size()).isEqualTo(0)
  }

  @Test
  fun `buffering a command reports nothing`() {
    // The connection indicator already says the app is offline, and the command is still expected
    // to happen, so a snackbar per stash would be noise.
    val buffer = buffer()

    buffer.stash(SocketMessage.create(Protocol.PlayerNext))
    buffer.drain()

    assertThat(discarded).isEmpty()
  }

  @Test
  fun `commands aged out of the buffer are reported once, with their count`() {
    val buffer = buffer(ttlMs = 10_000L)
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))
    buffer.stash(SocketMessage.create(Protocol.PlayerPrevious))

    now += 10_000L
    buffer.drain()

    assertThat(discarded).containsExactly(2)
  }

  @Test
  fun `commands pushed out by a full buffer are reported`() {
    val buffer = buffer(capacity = 1)

    buffer.stash(SocketMessage.create(Protocol.PlayerNext))
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))

    assertThat(discarded).containsExactly(1)
  }

  @Test
  fun `an explicit disconnect is not reported as a loss`() {
    val buffer = buffer()
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))

    buffer.clear()
    buffer.drain()

    assertThat(discarded).isEmpty()
  }
}
