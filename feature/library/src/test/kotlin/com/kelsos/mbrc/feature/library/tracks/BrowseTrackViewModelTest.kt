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
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.data.library.track.TrackRepository
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.LibrarySearchModel
import com.kelsos.mbrc.feature.library.domain.LibrarySyncUseCase
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
      single<LibrarySyncUseCase> { mockk(relaxed = true) }
      single<QueueHandler> { mockk(relaxed = true) }
      single<ConnectionStateFlow> { mockk(relaxed = true) }
      single<LibrarySettings> { mockk(relaxed = true) }
      single<LibrarySearchModel> { mockk(relaxed = true) }
      singleOf(::BrowseTrackViewModel)
    }

  private val viewModel: BrowseTrackViewModel by inject()
  private val repository: TrackRepository by inject()
  private val librarySyncUseCase: LibrarySyncUseCase by inject()
  private val queueHandler: QueueHandler by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()
  private val librarySettings: LibrarySettings by inject()
  private val searchModel: LibrarySearchModel by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    // Setup default mocks
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
  fun queueShouldEmitNetworkUnavailableWhenNotConnected() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns false
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

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Next, track)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(TrackUiMessage.NetworkUnavailable)
      }

      // Verify queue handler is not called when not connected
      coVerify(exactly = 0) { queueHandler.queueTrack(any<Track>(), any<Queue>()) }
      coVerify(exactly = 0) { queueHandler.queueTrack(any<Track>()) }
    }
  }

  @Test
  fun queueShouldEmitQueueSuccessWhenConnectedAndQueueSucceeds() {
    runTest(testDispatcher) {
      // Given
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

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Next, track)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(TrackUiMessage.QueueSuccess(1))
      }

      // Verify queue handler was called
      coVerify(exactly = 1) { queueHandler.queueTrack(track = track, type = Queue.Next) }
    }
  }

  @Test
  fun queueShouldEmitQueueFailedWhenConnectedButQueueFails() {
    runTest(testDispatcher) {
      // Given
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
      val queueResult = Outcome.Failure(AppError.OperationFailed)
      coEvery { queueHandler.queueTrack(track = track, type = Queue.Next) } returns queueResult

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Next, track)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(TrackUiMessage.QueueFailed)
      }

      // Verify queue handler was called
      coVerify(exactly = 1) { queueHandler.queueTrack(track = track, type = Queue.Next) }
    }
  }

  @Test
  fun queueShouldUseDefaultActionWhenQueueIsDefault() {
    runTest(testDispatcher) {
      // Given
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

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Default, track)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(TrackUiMessage.QueueSuccess(1))
      }

      // Verify queue handler was called with default action
      coVerify(exactly = 1) { queueHandler.queueTrack(track = track, type = Queue.Last) }
    }
  }

  @Test
  fun syncShouldEmitNetworkUnavailableWhenNotConnected() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns false

      // When & Then
      viewModel.events.test {
        viewModel.sync()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(TrackUiMessage.NetworkUnavailable)
      }

      // Verify sync use case is not called when not connected
      coVerify(exactly = 0) { librarySyncUseCase.sync() }
    }
  }

  @Test
  fun syncShouldCallSyncUseCaseWhenConnected() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns true

      // When & Then
      viewModel.events.test {
        viewModel.sync()
        testDispatcher.scheduler.advanceUntilIdle()

        // Should not emit any events for successful sync
        expectNoEvents()
      }

      // Verify sync use case was called
      coVerify(exactly = 1) { librarySyncUseCase.sync() }
    }
  }

  @Test
  fun networkCheckIsPerformedAtStartOfQueueOperation() {
    runTest(testDispatcher) {
      // Given - connection starts as true, then becomes false
      coEvery { connectionStateFlow.isConnected } returns true andThen false
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
      coEvery { queueHandler.queueTrack(any<Track>(), any<Queue>()) } returns queueResult

      // When & Then - First call should succeed, second should fail
      viewModel.events.test {
        viewModel.queue(Queue.Next, track) // Should succeed (first call)
        testDispatcher.scheduler.advanceUntilIdle()

        val firstEvent = awaitItem()
        assertThat(firstEvent).isEqualTo(TrackUiMessage.QueueSuccess(1))

        viewModel.queue(Queue.Next, track) // Should fail (second call)
        testDispatcher.scheduler.advanceUntilIdle()

        val secondEvent = awaitItem()
        assertThat(secondEvent).isEqualTo(TrackUiMessage.NetworkUnavailable)
      }

      // Verify queue handler was only called once (when connected)
      coVerify(exactly = 1) { queueHandler.queueTrack(any<Track>(), any<Queue>()) }
    }
  }

  @Test
  fun networkCheckIsPerformedAtStartOfSyncOperation() {
    runTest(testDispatcher) {
      // Given - connection starts as true, then becomes false
      coEvery { connectionStateFlow.isConnected } returns true andThen false

      // When & Then - First call should succeed, second should fail
      viewModel.events.test {
        viewModel.sync() // Should succeed (first call)
        testDispatcher.scheduler.advanceUntilIdle()

        // No events expected for successful sync
        expectNoEvents()

        viewModel.sync() // Should fail (second call)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(TrackUiMessage.NetworkUnavailable)
      }

      // Verify sync use case was only called once (when connected)
      coVerify(exactly = 1) { librarySyncUseCase.sync() }
    }
  }

  @Test
  fun sortPreferenceShouldEmitInitialValue() {
    runTest(testDispatcher) {
      // Given
      val expectedPreference = TrackSortPreference(TrackSortField.TITLE, SortOrder.ASC)

      // When & Then
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
      // Given
      val newPreference = TrackSortPreference(TrackSortField.ARTIST, SortOrder.DESC)

      // When
      viewModel.updateSortPreference(newPreference)
      testDispatcher.scheduler.advanceUntilIdle()

      // Then
      coVerify { librarySettings.setTrackSortPreference(newPreference) }
    }
  }
}
