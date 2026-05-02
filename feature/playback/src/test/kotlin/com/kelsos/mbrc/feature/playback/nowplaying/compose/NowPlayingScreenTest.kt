package com.kelsos.mbrc.feature.playback.nowplaying.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.core.data.nowplaying.NowPlaying
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class NowPlayingScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `empty state shows the empty message`() {
    composeTestRule.setEmptyContent()

    composeTestRule.onNodeWithText("Your queue is empty").assertIsDisplayed()
  }

  @Test
  fun `empty state is wrapped in a scrollable container so pull-to-refresh works`() {
    composeTestRule.setEmptyContent()

    // PullToRefreshBox requires its child to be vertically scrollable to detect
    // the pull gesture; without it the empty state never triggers refresh.
    composeTestRule.onNode(hasScrollAction()).assertExists()
  }
}

private fun ComposeContentTestRule.setEmptyContent() {
  setContent {
    val tracks = flowOf(
      PagingData.empty<NowPlaying>(
        sourceLoadStates = LoadStates(
          refresh = LoadState.NotLoading(endOfPaginationReached = true),
          prepend = LoadState.NotLoading(endOfPaginationReached = true),
          append = LoadState.NotLoading(endOfPaginationReached = true)
        )
      )
    ).collectAsLazyPagingItems()
    TestNowPlayingContent(tracks = tracks, trackCount = 0, isConnected = true)
  }
}

@Composable
private fun TestNowPlayingContent(
  tracks: LazyPagingItems<NowPlaying>,
  trackCount: Int,
  isConnected: Boolean
) {
  NowPlayingContent(
    tracks = tracks,
    playingTrackPath = "",
    trackCount = trackCount,
    isRefreshing = false,
    isConnected = isConnected,
    onRefresh = {},
    onTrackClick = {},
    onTrackRemove = {},
    onTrackMove = { _, _ -> },
    onDragEnd = {},
    onGoToAlbum = null,
    onGoToArtist = {}
  )
}
