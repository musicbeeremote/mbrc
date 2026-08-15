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
    capacity: Int = PendingCommandBuffer.DEFAULT_CAPACITY,
    notifyIntervalMs: Long = PendingCommandBuffer.DEFAULT_NOTIFY_INTERVAL_MS
  ) = PendingCommandBuffer(clock, ttlMs, capacity, notifyIntervalMs) { discarded.add(it) }

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
  fun `queue mutations keep the order the user performed them in`() {
    val buffer = buffer()
    val removeFirst = SocketMessage.create(Protocol.NowPlayingListRemove, 1)
    val removeSecond = SocketMessage.create(Protocol.NowPlayingListRemove, 2)
    buffer.stash(removeFirst)
    buffer.stash(removeSecond)
    buffer.stash(removeFirst)

    assertThat(buffer.drain()).containsExactly(removeFirst, removeSecond, removeFirst).inOrder()
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
    val first = SocketMessage.create(Protocol.NowPlayingListPlay, 1)
    val second = SocketMessage.create(Protocol.NowPlayingListPlay, 2)
    val third = SocketMessage.create(Protocol.NowPlayingListPlay, 3)

    buffer.stash(first)
    buffer.stash(second)
    buffer.stash(third)

    assertThat(buffer.drain()).containsExactly(second, third).inOrder()
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
    buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, 1))

    now += 10_000L
    buffer.drain()

    assertThat(discarded).containsExactly(2)
  }

  @Test
  fun `an overflowing buffer does not report once per tap`() {
    val buffer = buffer(capacity = 1, notifyIntervalMs = 10_000L)

    repeat(5) { index ->
      now += 1_000L
      buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, index))
    }

    // Four commands were pushed out, but the user is told once.
    assertThat(discarded).containsExactly(1)
  }

  @Test
  fun `losses suppressed by the notification window are added to the next report`() {
    val buffer = buffer(capacity = 1, notifyIntervalMs = 10_000L)

    repeat(5) { index ->
      now += 1_000L
      buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, index))
    }
    now += 10_000L
    buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, 99))

    // 1 reported immediately, then the 3 held back plus the one just pushed out.
    assertThat(discarded).containsExactly(1, 4).inOrder()
  }

  @Test
  fun `an explicit disconnect is not reported as a loss`() {
    val buffer = buffer(capacity = 1)
    buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, 1))
    buffer.stash(SocketMessage.create(Protocol.NowPlayingListPlay, 2))
    discarded.clear()

    buffer.clear()
    buffer.stash(SocketMessage.create(Protocol.PlayerNext))

    assertThat(discarded).isEmpty()
  }
}
