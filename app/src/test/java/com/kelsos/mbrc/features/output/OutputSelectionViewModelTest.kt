package com.kelsos.mbrc.features.output

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class OutputSelectionViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var viewmodel: OutputSelectionViewModel
  private lateinit var outputApi: OutputApi

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    outputApi = mockk()

    viewmodel = OutputSelectionViewModel(
      outputApi = outputApi,
      dispatchers = appCoroutineDispatchers
    )
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    testDispatcher.cleanupTestCoroutines()
  }

  @Test
  fun `originally view model should have empty values`() = runBlockingTest(testDispatcher) {
    viewmodel.outputs.test {
      expectNoEvents()
    }
  }

  @Test
  fun `after reload it should return the output information`() = runBlockingTest(testDispatcher) {
    val outputResponse = OutputResponse(
      devices = listOf("Output 1", "Output 2"),
      active = "Output 2"
    )
    coEvery { outputApi.getOutputs() } answers { outputResponse }

    viewmodel.outputs.test {
      viewmodel.reload()
      assertThat(expectItem()).isEqualTo(outputResponse)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if there is a socket timeout the emitter should have the proper result`() = runBlockingTest(
    testDispatcher
  ) {
    coEvery { outputApi.getOutputs() } throws SocketTimeoutException()

    viewmodel.emitter.test {
      viewmodel.reload()
      assertThat(expectItem()).isEqualTo(OutputSelectionResult.ConnectionError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if there is a socket exception the emitter should have a result`() = runBlockingTest(
    testDispatcher
  ) {
    coEvery { outputApi.getOutputs() } throws SocketException()

    viewmodel.outputs.test {
      viewmodel.reload()
      expectNoEvents()
    }

    viewmodel.emitter.test {
      viewmodel.reload()
      assertThat(expectItem()).isEqualTo(OutputSelectionResult.ConnectionError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if there is an exception the emitter should have the proper result`() = runBlockingTest(
    testDispatcher
  ) {
    coEvery { outputApi.getOutputs() } throws IOException()

    viewmodel.emitter.test {
      viewmodel.reload()
      assertThat(expectItem()).isEqualTo(OutputSelectionResult.UnknownError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if the user changes the output the result should update the live data`() = runBlockingTest(
    testDispatcher
  ) {
    val outputResponse = OutputResponse(
      devices = listOf("Output 1", "Output 2"),
      active = "Output 2"
    )

    coEvery { outputApi.setOutput(any()) } answers { outputResponse }

    viewmodel.outputs.test {
      viewmodel.setOutput("Output 2")
      assertThat(expectItem()).isEqualTo(outputResponse)
      cancelAndConsumeRemainingEvents()
    }

    viewmodel.emitter.test {
      viewmodel.setOutput("Output 2")
      assertThat(expectItem()).isEqualTo(OutputSelectionResult.Success)
      cancelAndConsumeRemainingEvents()
    }
  }
}
