package com.kelsos.mbrc.feature.playback.nowplaying.compose

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ScrollTargetIndexTest {

  @Test
  fun `leaves a couple of tracks visible above the playing one`() {
    assertThat(scrollTargetIndex(playingIndex = 50, itemCount = 100)).isEqualTo(48)
  }

  @Test
  fun `does not scroll past the top for a track near the start`() {
    assertThat(scrollTargetIndex(playingIndex = 0, itemCount = 100)).isEqualTo(0)
    assertThat(scrollTargetIndex(playingIndex = 1, itemCount = 100)).isEqualTo(0)
  }

  @Test
  fun `clamps to the last item when the index is beyond the loaded queue`() {
    // The index comes from the database while the count comes from the pager, so the two can
    // disagree for an instant after a track is removed.
    assertThat(scrollTargetIndex(playingIndex = 500, itemCount = 10)).isEqualTo(9)
  }

  @Test
  fun `returns the top for an empty queue`() {
    assertThat(scrollTargetIndex(playingIndex = 3, itemCount = 0)).isEqualTo(0)
  }
}
