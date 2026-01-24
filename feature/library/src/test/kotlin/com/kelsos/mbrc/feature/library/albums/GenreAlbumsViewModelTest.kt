package com.kelsos.mbrc.feature.library.albums

import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.settings.TrackAction
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
class GenreAlbumsViewModelTest : KoinTest {
  private val testModule =
    module {
      single<AlbumRepository> { mockk(relaxed = true) }
      single<QueueHandler> { mockk(relaxed = true) }
      single<ConnectionStateFlow> { mockk(relaxed = true) }
      single<LibrarySettings> { mockk(relaxed = true) }
      singleOf(::GenreAlbumsViewModel)
    }

  private val viewModel: GenreAlbumsViewModel by inject()
  private val repository: AlbumRepository by inject()
  private val queueHandler: QueueHandler by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()
  private val librarySettings: LibrarySettings by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    // Setup default mocks
    every { repository.getAlbumsByGenre(any()) } returns flowOf(PagingData.empty())
    every { librarySettings.libraryTrackDefaultActionFlow } returns flowOf(TrackAction.PlayNow)
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
      val album =
        Album(
          id = 1,
          artist = "Test Artist",
          album = "Test Album",
          cover = null
        )

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(AlbumUiMessage.NetworkUnavailable)
      }

      // Verify queue handler is not called when not connected
      coVerify(exactly = 0) { queueHandler.queueAlbum(any<Queue>(), any<String>(), any<String>()) }
    }
  }

  @Test
  fun queueShouldEmitQueueSuccessWhenConnectedAndQueueSucceeds() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns true
      val album =
        Album(
          id = 1,
          artist = "Test Artist",
          album = "Test Album",
          cover = null
        )
      val queueResult = Outcome.Success(10)
      coEvery { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") } returns
        queueResult

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(AlbumUiMessage.QueueSuccess(10))
      }

      // Verify queue handler was called
      coVerify(exactly = 1) { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") }
    }
  }

  @Test
  fun queueShouldEmitQueueFailedWhenConnectedButQueueFails() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns true
      val album =
        Album(
          id = 1,
          artist = "Test Artist",
          album = "Test Album",
          cover = null
        )
      val queueResult = Outcome.Failure(AppError.OperationFailed)
      coEvery { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") } returns
        queueResult

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(AlbumUiMessage.QueueFailed)
      }

      // Verify queue handler was called
      coVerify(exactly = 1) { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") }
    }
  }

  @Test
  fun queueShouldEmitOpenAlbumTracksWhenQueueIsDefault() {
    runTest(testDispatcher) {
      // Given
      val album =
        Album(
          id = 1,
          artist = "Test Artist",
          album = "Test Album",
          cover = null
        )

      // When & Then
      viewModel.events.test {
        viewModel.queue(Queue.Default, album)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(AlbumUiMessage.OpenAlbumTracks(album))
      }

      // Verify queue handler is not called for default action
      coVerify(exactly = 0) { queueHandler.queueAlbum(any<Queue>(), any<String>(), any<String>()) }
    }
  }
}
