package com.kelsos.mbrc.feature.library.tracks

import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.TrackAction
import com.kelsos.mbrc.core.common.settings.TrackSortField
import com.kelsos.mbrc.core.common.settings.TrackSortPreference
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.data.library.track.TrackRepository
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.LibrarySearchModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class BrowseTrackViewModelTest : KoinTest {
  private val searchTermFlow = MutableStateFlow("")

  private val testModule =
    module {
      single<TrackRepository> { mockk(relaxed = true) }
      single<QueueHandler> { mockk(relaxed = true) }
      single<ConnectionStateFlow> { mockk(relaxed = true) }
      single<LibrarySettings> { mockk(relaxed = true) }
      single<LibrarySearchModel> { mockk(relaxed = true) }
      singleOf(::BrowseTrackViewModel)
    }

  private val viewModel: BrowseTrackViewModel by inject()
  private val repository: TrackRepository by inject()
  private val queueHandler: QueueHandler by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()
  private val librarySettings: LibrarySettings by inject()
  private val searchModel: LibrarySearchModel by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    every { searchModel.term } returns searchTermFlow
    every { repository.getAll() } returns flowOf(PagingData.empty())
    every { repository.getAll(any(), any()) } returns flowOf(PagingData.empty())
    every { repository.search(any()) } returns flowOf(PagingData.empty())
    every { repository.search(any(), any(), any()) } returns flowOf(PagingData.empty())
    every { librarySettings.libraryTrackDefaultActionFlow } returns flowOf(TrackAction.PlayNow)
    every { librarySettings.trackSortPreferenceFlow } returns flowOf(
      TrackSortPreference(TrackSortField.TITLE, SortOrder.ASC)
    )
    coEvery { connectionStateFlow.isConnected } returns true
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun queueShouldEmitQueueSuccessWhenConnectedAndQueueSucceeds() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns true
      val track =
        Track(
          id = 1,
          artist = "Test Artist",
          title = "Test Track",
          album = "Test Album",
          year = "2023",
          genre = "Rock",
          disc = 1,
          trackno = 1,
          src = "",
          albumArtist = "Test Artist"
        )
      val queueResult = Outcome.Success(1)
      coEvery { queueHandler.queueTrack(track = track, type = Queue.Next) } returns queueResult

      viewModel.queueResults.test {
        viewModel.queue(Queue.Next, track)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(Outcome.Success(1))
      }

      coVerify(exactly = 1) { queueHandler.queueTrack(track = track, type = Queue.Next) }
    }
  }

  @Test
  fun queueShouldUseDefaultActionWhenQueueIsDefault() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns true
      every { librarySettings.libraryTrackDefaultActionFlow } returns flowOf(TrackAction.QueueLast)
      val track =
        Track(
          id = 1,
          artist = "Test Artist",
          title = "Test Track",
          album = "Test Album",
          year = "2023",
          genre = "Rock",
          disc = 1,
          trackno = 1,
          src = "",
          albumArtist = "Test Artist"
        )
      val queueResult = Outcome.Success(1)
      coEvery { queueHandler.queueTrack(track = track, type = Queue.Last) } returns queueResult

      viewModel.queueResults.test {
        viewModel.queue(Queue.Default, track)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(Outcome.Success(1))
      }

      coVerify(exactly = 1) { queueHandler.queueTrack(track = track, type = Queue.Last) }
    }
  }

  @Test
  fun sortPreferenceShouldEmitInitialValue() {
    runTest(testDispatcher) {
      val expectedPreference = TrackSortPreference(TrackSortField.TITLE, SortOrder.ASC)

      viewModel.sortPreference.test {
        val preference = awaitItem()
        assertThat(preference).isEqualTo(expectedPreference)
        cancelAndIgnoreRemainingEvents()
      }
    }
  }

  @Test
  fun updateSortPreferenceShouldCallLibrarySettings() {
    runTest(testDispatcher) {
      val newPreference = TrackSortPreference(TrackSortField.ARTIST, SortOrder.DESC)

      viewModel.updateSortPreference(newPreference)
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify { librarySettings.setTrackSortPreference(newPreference) }
    }
  }
}
