package com.kelsos.mbrc.feature.library

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The queue reporting every library view model inherits. Each view model's own test only
 * checks which request it hands to [BaseLibraryViewModel.launchQueue].
 */
@RunWith(AndroidJUnit4::class)
class BaseLibraryViewModelTest {
  private val connectionStateFlow: ConnectionStateFlow = mockk {
    every { isConnected } returns true
  }
  private val viewModel = TestLibraryViewModel(connectionStateFlow)

  @Test
  fun `queueing while disconnected reports the network and sends nothing`() {
    runTest(testDispatcher) {
      every { connectionStateFlow.isConnected } returns false
      var requested = false

      viewModel.queueResults.test {
        viewModel.queue { Outcome.Success(1).also { requested = true } }
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(Outcome.Failure(AppError.NetworkUnavailable))
      }

      assertThat(requested).isFalse()
    }
  }

  @Test
  fun `a successful queue reports the number of tracks added`() {
    runTest(testDispatcher) {
      viewModel.queueResults.test {
        viewModel.queue { Outcome.Success(12) }
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(Outcome.Success(12))
      }
    }
  }

  @Test
  fun `any failed queue is reported as an operation failure`() {
    runTest(testDispatcher) {
      viewModel.queueResults.test {
        viewModel.queue { Outcome.Failure(AppError.NetworkUnavailable) }
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(Outcome.Failure(AppError.OperationFailed))
      }
    }
  }

  @Test
  fun `the connection is checked again on every queue request`() {
    runTest(testDispatcher) {
      every { connectionStateFlow.isConnected } returns true andThen false

      viewModel.queueResults.test {
        viewModel.queue { Outcome.Success(3) }
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(Outcome.Success(3))

        viewModel.queue { Outcome.Success(3) }
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(Outcome.Failure(AppError.NetworkUnavailable))
      }
    }
  }

  private class TestLibraryViewModel(connectionStateFlow: ConnectionStateFlow) :
    BaseLibraryViewModel<Nothing>(connectionStateFlow) {
    fun queue(request: suspend () -> Outcome<Int>) = launchQueue(request)
  }
}
