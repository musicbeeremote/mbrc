package com.kelsos.mbrc.core.networking.protocol

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SelfMutationTrackerTest {

  private fun tracker(clock: Clock, windowMs: Long = 100L) =
    SelfMutationTracker(clock, SelfMutationConfig(windowMs = windowMs))

  @Test
  fun `wasRecentlyMarked returns false before any mark`() {
    val tracker = tracker(clock = { 1_000L })

    assertThat(tracker.wasRecentlyMarked()).isFalse()
  }

  @Test
  fun `wasRecentlyMarked returns true within the window after mark`() {
    var now = 1_000L
    val tracker = tracker(clock = { now })

    tracker.mark()
    now = 1_050L

    assertThat(tracker.wasRecentlyMarked()).isTrue()
  }

  @Test
  fun `wasRecentlyMarked returns false once the window elapses`() {
    var now = 1_000L
    val tracker = tracker(clock = { now })

    tracker.mark()
    now = 1_101L

    assertThat(tracker.wasRecentlyMarked()).isFalse()
  }

  @Test
  fun `mark resets the window so a later broadcast is still skipped`() {
    var now = 1_000L
    val tracker = tracker(clock = { now })

    tracker.mark()
    now = 1_080L
    tracker.mark()
    now = 1_170L

    assertThat(tracker.wasRecentlyMarked()).isTrue()
  }
}
