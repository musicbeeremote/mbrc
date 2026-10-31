package com.kelsos.mbrc.feature.library.albums

import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.AlbumViewMode
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.settings.SortOrder
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * The queue, sort and view mode behaviour every album list inherits from [BaseAlbumViewModel].
 *
 * A subclass provides [viewModelModule] with its view model and any collaborators beyond the four
 * declared here, and injects [viewModel] from it. Every [AlbumRepository] query returns an empty
 * page unless a test says otherwise; a subclass stubs the queries its own view model adds.
 */
abstract class AlbumViewModelContractTest<VM : BaseAlbumViewModel> : KoinTest {
  protected abstract val viewModelModule: Module
  protected abstract val viewModel: VM

  protected val repository: AlbumRepository by inject()
  protected val queueHandler: QueueHandler by inject()
  protected val connectionStateFlow: ConnectionStateFlow by inject()
  protected val librarySettings: LibrarySettings by inject()

  private val album = Album(id = 1, artist = "Test Artist", album = "Test Album", cover = null)

  @Before
  fun startContractKoin() {
    startKoin {
      modules(
        module {
          single<AlbumRepository> { mockk(relaxed = true) }
          single<QueueHandler> { mockk(relaxed = true) }
          single<ConnectionStateFlow> { mockk(relaxed = true) }
          single<LibrarySettings> { mockk(relaxed = true) }
        },
        viewModelModule,
        testDispatcherModule
      )
    }

    every { repository.getAll(any(), any()) } returns flowOf(PagingData.empty())
    every { repository.search(any(), any(), any()) } returns flowOf(PagingData.empty())
    every { librarySettings.libraryTrackDefaultActionFlow } returns flowOf(TrackAction.PlayNow)
    every { librarySettings.albumSortPreferenceFlow } returns flowOf(
      AlbumSortPreference(AlbumSortField.NAME, SortOrder.ASC)
    )
    coEvery { connectionStateFlow.isConnected } returns true
  }

  @After
  fun stopContractKoin() {
    stopKoin()
  }

  @Test
  fun `queueing while disconnected reports the network and queues nothing`() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns false

      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.NetworkUnavailable)
      }

      coVerify(exactly = 0) { queueHandler.queueAlbum(any(), any(), any()) }
    }
  }

  @Test
  fun `a successful queue reports the number of tracks added`() {
    runTest(testDispatcher) {
      coEvery { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") } returns
        Outcome.Success(10)

      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.QueueSuccess(10))
      }

      coVerify(exactly = 1) { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") }
    }
  }

  @Test
  fun `a failed queue reports the failure`() {
    runTest(testDispatcher) {
      coEvery { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") } returns
        Outcome.Failure(AppError.OperationFailed)

      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.QueueFailed)
      }

      coVerify(exactly = 1) { queueHandler.queueAlbum(Queue.Next, "Test Album", "Test Artist") }
    }
  }

  @Test
  fun `the default action opens the album instead of queueing it`() {
    runTest(testDispatcher) {
      viewModel.events.test {
        viewModel.queue(Queue.Default, album)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.OpenAlbumTracks(album))
      }

      coVerify(exactly = 0) { queueHandler.queueAlbum(any(), any(), any()) }
    }
  }

  @Test
  fun `the connection is checked again on every queue request`() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns true andThen false
      coEvery { queueHandler.queueAlbum(any(), any(), any()) } returns Outcome.Success(10)

      viewModel.events.test {
        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.QueueSuccess(10))

        viewModel.queue(Queue.Next, album)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.NetworkUnavailable)
      }

      coVerify(exactly = 1) { queueHandler.queueAlbum(any(), any(), any()) }
    }
  }

  @Test
  fun `the sort preference comes from the library settings`() {
    runTest(testDispatcher) {
      viewModel.sortPreference.test {
        assertThat(awaitItem()).isEqualTo(AlbumSortPreference(AlbumSortField.NAME, SortOrder.ASC))
        cancelAndIgnoreRemainingEvents()
      }
    }
  }

  @Test
  fun `a new sort preference is saved to the library settings`() {
    runTest(testDispatcher) {
      val preference = AlbumSortPreference(AlbumSortField.ARTIST, SortOrder.DESC)

      viewModel.updateSortPreference(preference)
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify(exactly = 1) { librarySettings.setAlbumSortPreference(preference) }
    }
  }

  @Test
  fun `toggling from the list saves the grid`() {
    runTest(testDispatcher) {
      every { librarySettings.albumViewModeFlow } returns flowOf(AlbumViewMode.LIST)

      viewModel.toggleViewMode()
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify(exactly = 1) { librarySettings.setAlbumViewMode(AlbumViewMode.GRID) }
    }
  }
}
