package com.kelsos.mbrc.features.output

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.rules.CoroutineTestRule
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  private lateinit var viewmodel: OutputSelectionViewModel
  private lateinit var outputApi: OutputApi

  @Before
  fun setUp() {
    outputApi = mockk()

    viewmodel = OutputSelectionViewModel(
      outputApi = outputApi,
      dispatchers = appCoroutineDispatchers
    )
  }

  @Test
  fun `originally view model should have empty values`() = runTest {
    viewmodel.outputs.test {
      expectNoEvents()
    }
  }

  @Test
  fun `after reload it should return the output information`() = runTest {
    val outputResponse = OutputResponse(
      devices = listOf("Output 1", "Output 2"),
      active = "Output 2"
    )

    coEvery { outputApi.getOutputs() } answers { outputResponse.right() }

    viewmodel.outputs.test {
      viewmodel.reload()
      assertThat(awaitItem()).isEqualTo(outputResponse)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if there is a socket timeout the emitter should have the proper result`() = runTest {
    coEvery { outputApi.getOutputs() } answers { SocketTimeoutException().left() }

    viewmodel.emitter.test {
      viewmodel.reload()
      assertThat(awaitItem()).isEqualTo(OutputSelectionResult.ConnectionError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if there is a socket exception the emitter should have a result`() = runTest {
    coEvery { outputApi.getOutputs() } answers { SocketException().left() }

    viewmodel.outputs.test {
      viewmodel.reload()
      expectNoEvents()
    }

    viewmodel.emitter.test {
      viewmodel.reload()
      assertThat(awaitItem()).isEqualTo(OutputSelectionResult.ConnectionError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if there is an exception the emitter should have the proper result`() = runTest {
    coEvery { outputApi.getOutputs() } answers { IOException().left() }

    viewmodel.emitter.test {
      viewmodel.reload()
      assertThat(awaitItem()).isEqualTo(OutputSelectionResult.UnknownError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `if the user changes the output the result should update the live data`() = runTest {
    val outputResponse = OutputResponse(
      devices = listOf("Output 1", "Output 2"),
      active = "Output 2"
    )

    coEvery { outputApi.setOutput(any()) } answers { outputResponse.right() }

    viewmodel.outputs.test {
      viewmodel.setOutput("Output 2")
      assertThat(awaitItem()).isEqualTo(outputResponse)
      cancelAndConsumeRemainingEvents()
    }

    viewmodel.emitter.test {
      viewmodel.setOutput("Output 2")
      assertThat(awaitItem()).isEqualTo(OutputSelectionResult.Success)
      cancelAndConsumeRemainingEvents()
    }
  }
}
