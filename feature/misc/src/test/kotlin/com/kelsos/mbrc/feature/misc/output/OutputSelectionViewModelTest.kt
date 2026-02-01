package com.kelsos.mbrc.feature.misc.output

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.networking.api.OutputApi
import com.kelsos.mbrc.core.networking.dto.OutputResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OutputSelectionViewModelTest : KoinTest {

  private lateinit var viewModel: OutputSelectionViewModel
  private lateinit var outputApi: OutputApi

  private val testModule = module {
    single { outputApi }
  }

  @Before
  fun setUp() {
    outputApi = mockk(relaxed = true)

    startKoin { modules(listOf(testModule, testDispatcherModule)) }

    viewModel = OutputSelectionViewModel(
      outputApi = outputApi,
      dispatchers = org.koin.java.KoinJavaComponent.get(
        com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers::class.java
      )
    )
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  // region reload() Tests

  @Test
  fun `reload should emit outputs on success`() = runTest(testDispatcher) {
    val expectedResponse = OutputResponse(
      devices = listOf("Speakers", "Headphones", "HDMI Output"),
      active = "Speakers"
    )
    coEvery { outputApi.getOutputs() } returns expectedResponse

    viewModel.outputs.test {
      viewModel.reload()
      advanceUntilIdle()

      val response = awaitItem()
      assertThat(response.devices).containsExactly("Speakers", "Headphones", "HDMI Output")
      assertThat(response.active).isEqualTo("Speakers")
    }

    coVerify { outputApi.getOutputs() }
  }

  @Test
  fun `reload should emit empty devices list when no outputs available`() =
    runTest(testDispatcher) {
      val expectedResponse = OutputResponse(devices = emptyList(), active = "")
      coEvery { outputApi.getOutputs() } returns expectedResponse

      viewModel.outputs.test {
        viewModel.reload()
        advanceUntilIdle()

        val response = awaitItem()
        assertThat(response.devices).isEmpty()
        assertThat(response.active).isEmpty()
      }
    }

  @Test
  fun `reload should emit ConnectionRefused error on SocketException`() = runTest(testDispatcher) {
    coEvery { outputApi.getOutputs() } throws IOException(SocketException("Connection refused"))

    viewModel.events.test {
      viewModel.reload()
      advanceUntilIdle()

      val event = awaitItem()
      assertThat(event).isInstanceOf(Outcome.Failure::class.java)
      assertThat((event as Outcome.Failure).error).isEqualTo(AppError.ConnectionRefused)
    }
  }

  @Test
  fun `reload should emit NetworkTimeout error on SocketTimeoutException`() =
    runTest(testDispatcher) {
      coEvery { outputApi.getOutputs() } throws IOException(SocketTimeoutException("Timeout"))

      viewModel.events.test {
        viewModel.reload()
        advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isInstanceOf(Outcome.Failure::class.java)
        assertThat((event as Outcome.Failure).error).isEqualTo(AppError.NetworkTimeout)
      }
    }

  @Test
  fun `reload should emit Unknown error on generic IOException`() = runTest(testDispatcher) {
    val ioException = IOException("Some IO error")
    coEvery { outputApi.getOutputs() } throws ioException

    viewModel.events.test {
      viewModel.reload()
      advanceUntilIdle()

      val event = awaitItem()
      assertThat(event).isInstanceOf(Outcome.Failure::class.java)
      val error = (event as Outcome.Failure).error
      assertThat(error).isInstanceOf(AppError.Unknown::class.java)
      assertThat((error as AppError.Unknown).cause).isEqualTo(ioException)
    }
  }

  @Test
  fun `reload should call outputApi getOutputs`() = runTest(testDispatcher) {
    coEvery { outputApi.getOutputs() } returns OutputResponse()

    viewModel.reload()
    advanceUntilIdle()

    coVerify(exactly = 1) { outputApi.getOutputs() }
  }

  @Test
  fun `multiple reload calls should each call outputApi`() = runTest(testDispatcher) {
    coEvery { outputApi.getOutputs() } returns OutputResponse()

    viewModel.reload()
    viewModel.reload()
    viewModel.reload()
    advanceUntilIdle()

    coVerify(exactly = 3) { outputApi.getOutputs() }
  }

  // endregion

  // region setOutput() Tests

  @Test
  fun `setOutput should emit updated outputs on success`() = runTest(testDispatcher) {
    val expectedResponse = OutputResponse(
      devices = listOf("Speakers", "Headphones"),
      active = "Headphones"
    )
    coEvery { outputApi.setOutput("Headphones") } returns expectedResponse

    viewModel.outputs.test {
      viewModel.setOutput("Headphones")
      advanceUntilIdle()

      val response = awaitItem()
      assertThat(response.active).isEqualTo("Headphones")
    }

    coVerify { outputApi.setOutput("Headphones") }
  }

  @Test
  fun `setOutput should call outputApi setOutput with correct device name`() =
    runTest(testDispatcher) {
      coEvery { outputApi.setOutput(any()) } returns OutputResponse()

      viewModel.setOutput("HDMI Output")
      advanceUntilIdle()

      coVerify { outputApi.setOutput("HDMI Output") }
    }

  @Test
  fun `setOutput should emit ConnectionRefused error on SocketException`() =
    runTest(testDispatcher) {
      coEvery { outputApi.setOutput(any()) } throws
        IOException(SocketException("Connection refused"))

      viewModel.events.test {
        viewModel.setOutput("Headphones")
        advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isInstanceOf(Outcome.Failure::class.java)
        assertThat((event as Outcome.Failure).error).isEqualTo(AppError.ConnectionRefused)
      }
    }

  @Test
  fun `setOutput should emit NetworkTimeout error on SocketTimeoutException`() =
    runTest(testDispatcher) {
      coEvery { outputApi.setOutput(any()) } throws IOException(SocketTimeoutException("Timeout"))

      viewModel.events.test {
        viewModel.setOutput("Headphones")
        advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isInstanceOf(Outcome.Failure::class.java)
        assertThat((event as Outcome.Failure).error).isEqualTo(AppError.NetworkTimeout)
      }
    }

  @Test
  fun `setOutput should emit Unknown error on generic IOException`() = runTest(testDispatcher) {
    val ioException = IOException("Network error")
    coEvery { outputApi.setOutput(any()) } throws ioException

    viewModel.events.test {
      viewModel.setOutput("Headphones")
      advanceUntilIdle()

      val event = awaitItem()
      assertThat(event).isInstanceOf(Outcome.Failure::class.java)
      val error = (event as Outcome.Failure).error
      assertThat(error).isInstanceOf(AppError.Unknown::class.java)
    }
  }

  @Test
  fun `setOutput with empty string should still call api`() = runTest(testDispatcher) {
    coEvery { outputApi.setOutput(any()) } returns OutputResponse()

    viewModel.setOutput("")
    advanceUntilIdle()

    coVerify { outputApi.setOutput("") }
  }

  // endregion

  // region Error Mapping Tests

  @Test
  fun `error mapping should handle nested SocketException cause`() = runTest(testDispatcher) {
    val socketException = SocketException("Connection refused")
    val ioException = IOException("Wrapper", socketException)
    coEvery { outputApi.getOutputs() } throws ioException

    viewModel.events.test {
      viewModel.reload()
      advanceUntilIdle()

      val event = awaitItem()
      assertThat((event as Outcome.Failure).error).isEqualTo(AppError.ConnectionRefused)
    }
  }

  @Test
  fun `error mapping should handle nested SocketTimeoutException cause`() =
    runTest(testDispatcher) {
      val timeoutException = SocketTimeoutException("Timed out")
      val ioException = IOException("Wrapper", timeoutException)
      coEvery { outputApi.getOutputs() } throws ioException

      viewModel.events.test {
        viewModel.reload()
        advanceUntilIdle()

        val event = awaitItem()
        assertThat((event as Outcome.Failure).error).isEqualTo(AppError.NetworkTimeout)
      }
    }

  @Test
  fun `error mapping should return Unknown for null cause`() = runTest(testDispatcher) {
    val ioException = IOException("No cause")
    coEvery { outputApi.getOutputs() } throws ioException

    viewModel.events.test {
      viewModel.reload()
      advanceUntilIdle()

      val event = awaitItem()
      val error = (event as Outcome.Failure).error
      assertThat(error).isInstanceOf(AppError.Unknown::class.java)
    }
  }

  // endregion

  // region Integration Tests

  @Test
  fun `reload followed by setOutput should emit both responses`() = runTest(testDispatcher) {
    val reloadResponse = OutputResponse(
      devices = listOf("Speakers", "Headphones"),
      active = "Speakers"
    )
    val setOutputResponse = OutputResponse(
      devices = listOf("Speakers", "Headphones"),
      active = "Headphones"
    )

    coEvery { outputApi.getOutputs() } returns reloadResponse
    coEvery { outputApi.setOutput("Headphones") } returns setOutputResponse

    viewModel.outputs.test {
      viewModel.reload()
      advanceUntilIdle()

      val firstResponse = awaitItem()
      assertThat(firstResponse.active).isEqualTo("Speakers")

      viewModel.setOutput("Headphones")
      advanceUntilIdle()

      val secondResponse = awaitItem()
      assertThat(secondResponse.active).isEqualTo("Headphones")
    }
  }

  @Test
  fun `error on reload should not prevent subsequent setOutput`() = runTest(testDispatcher) {
    coEvery { outputApi.getOutputs() } throws IOException("Error")
    coEvery { outputApi.setOutput(any()) } returns OutputResponse(active = "Headphones")

    // First call fails
    viewModel.events.test {
      viewModel.reload()
      advanceUntilIdle()
      awaitItem() // Consume error event
    }

    // Second call should succeed
    viewModel.outputs.test {
      viewModel.setOutput("Headphones")
      advanceUntilIdle()

      val response = awaitItem()
      assertThat(response.active).isEqualTo("Headphones")
    }
  }

  // endregion
}
