package com.kelsos.mbrc.feature.content.radio

import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.AppState
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.radio.RadioRepository
import com.kelsos.mbrc.core.data.radio.RadioStation
import com.kelsos.mbrc.core.queue.PathQueueUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
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
import org.koin.test.get
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class RadioViewModelTest : KoinTest {
  private val testModule =
    module {
      single<RadioRepository> { mockk(relaxed = true) }
      single<PathQueueUseCase> { mockk(relaxed = true) }
      single<ConnectionStateFlow> { mockk(relaxed = true) }
      single<AppState> {
        mockk(relaxed = true) {
          every { playingTrack } returns MutableStateFlow(BasicTrackInfo())
        }
      }
      singleOf(::RadioViewModel)
    }

  private val viewModel: RadioViewModel by inject()
  private val radioRepository: RadioRepository by inject()
  private val pathQueueUseCase: PathQueueUseCase by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    // Setup default mocks
    val radioRepository: RadioRepository = get()
    val connectionStateFlow: ConnectionStateFlow = get()

    every { radioRepository.getAll() } returns flowOf(PagingData.empty())
    coEvery { connectionStateFlow.isConnected } returns true
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun reloadShouldEmitNetworkUnavailableWhenNotConnected() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns false

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.reload()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.NetworkUnavailable)
      }

      // Verify repository is not called when not connected
      coVerify(exactly = 0) { radioRepository.getRemote() }
    }
  }

  @Test
  fun reloadShouldEmitRefreshSuccessWhenConnectedAndRepositorySucceeds() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { radioRepository.getRemote() } returns Unit

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.reload()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.RefreshSuccess)
      }

      // Verify repository was called
      coVerify(exactly = 1) { radioRepository.getRemote() }
    }
  }

  @Test
  fun reloadShouldEmitRefreshFailedWhenConnectedButRepositoryThrowsIOException() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { radioRepository.getRemote() } throws IOException("Network error")

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.reload()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.RefreshFailed)
      }

      // Verify repository was called
      coVerify(exactly = 1) { radioRepository.getRemote() }
    }
  }

  @Test
  fun playShouldEmitNetworkUnavailableWhenNotConnected() {
    runTest(testDispatcher) {
      // Given
      val radioPath = "http://example.com/radio"
      coEvery { connectionStateFlow.isConnected } returns false

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.play(radioPath)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.NetworkUnavailable)
      }

      // Verify queue use case is not called when not connected
      coVerify(exactly = 0) { pathQueueUseCase.queuePath(any()) }
    }
  }

  @Test
  fun playShouldEmitQueueSuccessWhenConnectedAndQueueSucceeds() {
    runTest(testDispatcher) {
      // Given
      val radioPath = "http://example.com/radio"
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { pathQueueUseCase.queuePath(radioPath) } returns Outcome.Success(1)

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.play(radioPath)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.QueueSuccess)
      }

      // Verify queue use case was called with correct path
      coVerify(exactly = 1) { pathQueueUseCase.queuePath(radioPath) }
    }
  }

  @Test
  fun playShouldEmitQueueFailedWhenConnectedButQueueFails() {
    runTest(testDispatcher) {
      // Given
      val radioPath = "http://example.com/radio"
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { pathQueueUseCase.queuePath(radioPath) } returns
        Outcome.Failure(AppError.OperationFailed)

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.play(radioPath)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.QueueFailed)
      }

      // Verify queue use case was called with correct path
      coVerify(exactly = 1) { pathQueueUseCase.queuePath(radioPath) }
    }
  }

  @Test
  fun actionsShouldDelegateToViewModelMethods() {
    runTest(testDispatcher) {
      // Given
      val radioPath = "http://example.com/radio"
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { pathQueueUseCase.queuePath(radioPath) } returns Outcome.Success(1)
      coEvery { radioRepository.getRemote() } returns Unit

      // Test actions.play delegates to play method
      viewModel.state.events.test {
        viewModel.actions.play(radioPath)
        testDispatcher.scheduler.advanceUntilIdle()

        val playEvent = awaitItem()
        assertThat(playEvent).isEqualTo(RadioUiMessages.QueueSuccess)

        // Test actions.reload delegates to reload method
        viewModel.actions.reload()
        testDispatcher.scheduler.advanceUntilIdle()

        val reloadEvent = awaitItem()
        assertThat(reloadEvent).isEqualTo(RadioUiMessages.RefreshSuccess)
      }
    }
  }

  @Test
  fun radiosShouldReturnRepositoryPagingData() {
    // Given
    val radioRepository: RadioRepository = get()
    val mockPagingData = PagingData.empty<RadioStation>()
    every { radioRepository.getAll() } returns flowOf(mockPagingData)

    // Then
    assertThat(viewModel.state.radios).isNotNull()
    // Note: PagingData testing requires more setup, this verifies the flow is accessible
  }

  @Test
  fun multipleReloadCallsShouldEmitMultipleEvents() {
    runTest(testDispatcher) {
      // Given
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { radioRepository.getRemote() } returns Unit

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.reload()
        viewModel.actions.reload()
        testDispatcher.scheduler.advanceUntilIdle()

        val firstEvent = awaitItem()
        val secondEvent = awaitItem()

        assertThat(firstEvent).isEqualTo(RadioUiMessages.RefreshSuccess)
        assertThat(secondEvent).isEqualTo(RadioUiMessages.RefreshSuccess)
      }

      // Verify repository was called twice
      coVerify(exactly = 2) { radioRepository.getRemote() }
    }
  }

  @Test
  fun playWithEmptyPathShouldStillCallQueueHandler() {
    runTest(testDispatcher) {
      // Given
      val emptyPath = ""
      coEvery { connectionStateFlow.isConnected } returns true
      coEvery { pathQueueUseCase.queuePath(emptyPath) } returns
        Outcome.Failure(AppError.OperationFailed)

      // When & Then
      viewModel.state.events.test {
        viewModel.actions.play(emptyPath)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(RadioUiMessages.QueueFailed)
      }

      // Verify queue use case was called even with empty path
      coVerify(exactly = 1) { pathQueueUseCase.queuePath(emptyPath) }
    }
  }

  @Test
  fun networkCheckIsPerformedAtStartOfOperation() {
    runTest(testDispatcher) {
      // Given - connection starts as true, then becomes false
      coEvery { connectionStateFlow.isConnected } returns true andThen false
      coEvery { radioRepository.getRemote() } returns Unit

      // When & Then - First call should succeed, second should fail
      viewModel.state.events.test {
        viewModel.actions.reload() // Should succeed (first call)
        testDispatcher.scheduler.advanceUntilIdle()

        val firstEvent = awaitItem()
        assertThat(firstEvent).isEqualTo(RadioUiMessages.RefreshSuccess)

        viewModel.actions.reload() // Should fail (second call)
        testDispatcher.scheduler.advanceUntilIdle()

        val secondEvent = awaitItem()
        assertThat(secondEvent).isEqualTo(RadioUiMessages.NetworkUnavailable)
      }
    }
  }
}
