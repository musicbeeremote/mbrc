package com.kelsos.mbrc.feature.library.compose

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.paging.PagingData
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.AlbumViewMode
import com.kelsos.mbrc.core.common.settings.ArtistSortField
import com.kelsos.mbrc.core.common.settings.GenreSortField
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.SortPreference
import com.kelsos.mbrc.core.common.settings.TrackSortField
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.ui.R
import com.kelsos.mbrc.feature.library.albums.AlbumInfo
import com.kelsos.mbrc.feature.library.albums.ArtistAlbumsViewModel
import com.kelsos.mbrc.feature.library.albums.BrowseAlbumViewModel
import com.kelsos.mbrc.feature.library.artists.BrowseArtistViewModel
import com.kelsos.mbrc.feature.library.artists.GenreArtistsViewModel
import com.kelsos.mbrc.feature.library.compose.drilldown.AlbumTracksScreen
import com.kelsos.mbrc.feature.library.compose.drilldown.ArtistAlbumsScreen
import com.kelsos.mbrc.feature.library.compose.drilldown.GenreArtistsScreen
import com.kelsos.mbrc.feature.library.compose.tabs.AlbumsTab
import com.kelsos.mbrc.feature.library.compose.tabs.ArtistsTab
import com.kelsos.mbrc.feature.library.compose.tabs.GenresTab
import com.kelsos.mbrc.feature.library.compose.tabs.TracksTab
import com.kelsos.mbrc.feature.library.genres.BrowseGenreViewModel
import com.kelsos.mbrc.feature.library.tracks.AlbumTracksViewModel
import com.kelsos.mbrc.feature.library.tracks.BrowseTrackViewModel
import com.kelsos.mbrc.feature.minicontrol.MiniControlState
import com.kelsos.mbrc.feature.minicontrol.MiniControlViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner

/**
 * Each library tab and drilldown hands its view model's `queueResults` to `QueueResultEffect`. The
 * view models are relaxed mocks; a test stubs only the flows its screen collects. Drilldowns embed
 * `MiniControl`, which resolves its own view model from Koin, so a stubbed one is registered.
 */
@RunWith(RobolectricTestRunner::class)
class ScreenQueueResultsTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val snackbarHostState = SnackbarHostState()

  @Before
  fun startMiniControlKoin() {
    startKoin {
      modules(
        module {
          viewModel {
            mockk<MiniControlViewModel>(relaxed = true) {
              every { state } returns flowOf(MiniControlState())
              every { events } returns emptyFlow()
            }
          }
        }
      )
    }
  }

  @After
  fun stopMiniControlKoin() {
    stopKoin()
  }

  @Test
  fun `the tracks tab shows how many tracks were queued`() {
    val viewModel = mockk<BrowseTrackViewModel>(relaxed = true) {
      every { tracks } returns flowOf(PagingData.empty())
      every { showSync } returns flowOf(true)
      every { sortPreference } returns flowOf(SortPreference(TrackSortField.TITLE, SortOrder.ASC))
      every { queueResults } returns flowOf(Outcome.Success(3))
    }

    assertSnackbar(queuedMessage(3)) { tab ->
      TracksTab(tab, isSyncing = false, showSortSheet = false, onDismissSortSheet = {
      }, onSync = {}, viewModel = viewModel)
    }
  }

  @Test
  fun `the albums tab reports a failed queue`() {
    val viewModel = mockk<BrowseAlbumViewModel>(relaxed = true) {
      every { albums } returns flowOf(PagingData.empty())
      every { events } returns emptyFlow()
      every { showSync } returns flowOf(true)
      every { sortPreference } returns flowOf(SortPreference(AlbumSortField.NAME, SortOrder.ASC))
      every { queueResults } returns flowOf(Outcome.Failure(AppError.OperationFailed))
    }

    assertSnackbar(context.getString(R.string.queue_result__failure)) { tab ->
      AlbumsTab(
        tab,
        isSyncing = false,
        showSortSheet = false,
        isGridMode = false,
        onNavigateToAlbumTracks = {},
        onDismissSortSheet = {},
        onSync = {},
        viewModel = viewModel
      )
    }
  }

  @Test
  fun `the artists tab reports an unreachable network`() {
    val viewModel = mockk<BrowseArtistViewModel>(relaxed = true) {
      every { artists } returns flowOf(PagingData.empty())
      every { events } returns emptyFlow()
      every { showSync } returns flowOf(true)
      every { sortPreference } returns flowOf(SortPreference(ArtistSortField.NAME, SortOrder.ASC))
      every { queueResults } returns flowOf(Outcome.Failure(AppError.NetworkUnavailable))
    }

    assertSnackbar(context.getString(R.string.connection_error_network_unavailable)) { tab ->
      ArtistsTab(
        tab,
        isSyncing = false,
        showSortSheet = false,
        onNavigateToArtistAlbums = {},
        onDismissSortSheet = {},
        onSync = {},
        viewModel = viewModel
      )
    }
  }

  @Test
  fun `the genres tab shows how many tracks were queued`() {
    val viewModel = mockk<BrowseGenreViewModel>(relaxed = true) {
      every { genres } returns flowOf(PagingData.empty())
      every { events } returns emptyFlow()
      every { showSync } returns flowOf(true)
      every { sortPreference } returns flowOf(SortPreference(GenreSortField.NAME, SortOrder.ASC))
      every { queueResults } returns flowOf(Outcome.Success(1))
    }

    assertSnackbar(queuedMessage(1)) { tab ->
      GenresTab(
        tab,
        isSyncing = false,
        showSortSheet = false,
        onNavigateToGenreArtists = {},
        onNavigateToGenreAlbums = {},
        onDismissSortSheet = {},
        onSync = {},
        viewModel = viewModel
      )
    }
  }

  @Test
  fun `the artist albums screen shows how many tracks were queued`() {
    val viewModel = mockk<ArtistAlbumsViewModel>(relaxed = true) {
      every { albums } returns flowOf(PagingData.empty())
      every { events } returns emptyFlow()
      every { sortPreference } returns flowOf(SortPreference(AlbumSortField.NAME, SortOrder.ASC))
      every { albumViewMode } returns flowOf(AlbumViewMode.LIST)
      every { queueResults } returns flowOf(Outcome.Success(4))
    }

    assertSnackbar(queuedMessage(4)) { screen ->
      ArtistAlbumsScreen(
        artistName = "Artist",
        onNavigateBack = {},
        onNavigateToAlbumTracks = {},
        onNavigateToPlayer = {},
        snackbarHostState = screen,
        viewModel = viewModel
      )
    }
  }

  @Test
  fun `the album tracks screen reports a failed queue`() {
    val viewModel = mockk<AlbumTracksViewModel>(relaxed = true) {
      every { tracks } returns flowOf(PagingData.empty())
      every { queueResults } returns flowOf(Outcome.Failure(AppError.OperationFailed))
    }

    assertSnackbar(context.getString(R.string.queue_result__failure)) { screen ->
      AlbumTracksScreen(
        albumInfo = AlbumInfo(album = "Album", artist = "Artist", cover = null),
        onNavigateBack = {},
        onNavigateToPlayer = {},
        snackbarHostState = screen,
        viewModel = viewModel
      )
    }
  }

  @Test
  fun `the genre artists screen reports an unreachable network`() {
    val viewModel = mockk<GenreArtistsViewModel>(relaxed = true) {
      every { artists } returns flowOf(PagingData.empty())
      every { events } returns emptyFlow()
      every { sortPreference } returns flowOf(SortOrder.ASC)
      every { queueResults } returns flowOf(Outcome.Failure(AppError.NetworkUnavailable))
    }

    assertSnackbar(context.getString(R.string.connection_error_network_unavailable)) { screen ->
      GenreArtistsScreen(
        genreId = 1,
        genreName = "Jazz",
        onNavigateBack = {},
        onNavigateToArtistAlbums = {},
        onNavigateToPlayer = {},
        snackbarHostState = screen,
        viewModel = viewModel
      )
    }
  }

  private fun assertSnackbar(expected: String, tab: @Composable (SnackbarHostState) -> Unit) {
    composeRule.setContent { tab(snackbarHostState) }

    composeRule.waitUntil { snackbarHostState.currentSnackbarData != null }
    assertThat(snackbarHostState.currentSnackbarData?.visuals?.message).isEqualTo(expected)
  }

  private fun queuedMessage(count: Int): String = context.getString(
    R.string.queue_result__success,
    context.resources.getQuantityString(R.plurals.track, count, count)
  )
}
