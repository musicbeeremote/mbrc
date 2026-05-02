package com.kelsos.mbrc.feature.playback.nowplaying.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
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

  @Test
  fun `scrolling past the initial page triggers paging beyond the initial load size`() {
    val totalItems = 400
    val source = CountingPagingSource(totalItems = totalItems)
    val pager = Pager(
      config = PagingConfig(
        pageSize = 50,
        initialLoadSize = 100,
        prefetchDistance = 25,
        enablePlaceholders = false
      ),
      pagingSourceFactory = { source }
    )

    composeTestRule.setContent {
      TestNowPlayingContent(
        tracks = pager.flow.collectAsLazyPagingItems(),
        trackCount = totalItems,
        isConnected = false
      )
    }

    composeTestRule.waitUntil(timeoutMillis = 5_000) { source.loadCount >= 1 }
    val initialLoads = source.loadCount

    for (i in 0 until 20) {
      if (source.loadCount > initialLoads) break
      composeTestRule.onRoot().performTouchInput { swipeUp() }
      composeTestRule.waitForIdle()
    }

    assertThat(source.loadCount).isGreaterThan(initialLoads)
  }
}

private class CountingPagingSource(private val totalItems: Int) : PagingSource<Int, NowPlaying>() {
  @Volatile var loadCount: Int = 0
    private set

  override fun getRefreshKey(state: PagingState<Int, NowPlaying>): Int? = null

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NowPlaying> {
    loadCount += 1
    val key = params.key ?: 0
    val end = (key + params.loadSize).coerceAtMost(totalItems)
    val data = (key until end).map { i ->
      NowPlaying(
        id = i.toLong() + 1,
        title = "Track ${i + 1}",
        artist = "Artist ${i + 1}",
        path = "/path/$i",
        position = i + 1
      )
    }
    val nextKey = if (end >= totalItems) null else end
    val prevKey = if (key == 0) null else (key - params.loadSize).coerceAtLeast(0)
    return LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
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
