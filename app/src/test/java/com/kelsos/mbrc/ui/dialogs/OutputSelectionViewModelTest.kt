package com.kelsos.mbrc.ui.dialogs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputResponse
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.observeOnce
import io.mockk.coEvery
import io.mockk.mockk
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

  private lateinit var viewModel: OutputSelectionViewModel
  private lateinit var outputApi: OutputApi

  @Before
  fun setUp() {
    outputApi = mockk()

    viewModel = OutputSelectionViewModel(
      outputApi = outputApi,
      dispatchers = TestDispatchers.dispatchers
    )
  }

  @Test
  fun `originally view model should have empty values`() {
    viewModel.outputs.observeOnce {
      assertThat(it).isEmpty()
    }
    viewModel.selection.observeOnce {
      assertThat(it).isEmpty()
    }
  }

  @Test
  fun `after reload it should return the output information`() {
    coEvery { outputApi.getOutputs() } coAnswers {
      Either.right(
        OutputResponse(
          devices = listOf("Output 1", "Output 2"),
          active = "Output 2"
        )
      )
    }

    viewModel.reload()
    viewModel.outputs.observeOnce {
      assertThat(it).containsExactlyElementsIn(listOf("Output 1", "Output 2"))
    }
    viewModel.selection.observeOnce {
      assertThat(it).isEqualTo("Output 2")
    }
    viewModel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.Success)
    }
  }

  @Test
  fun `if there is a socket timeout the emitter should have the proper result`() {
    coEvery { outputApi.getOutputs() } coAnswers {
      Either.left(SocketTimeoutException())
    }

    viewModel.reload()

    viewModel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.ConnectionError)
    }
  }

  @Test
  fun `if there is a socket exception the emitter should have the proper result`() {
    coEvery { outputApi.getOutputs() } coAnswers {
      Either.left(SocketException())
    }

    viewModel.reload()

    viewModel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.ConnectionError)
    }
  }

  @Test
  fun `if there is an exception the emitter should have the proper result`() {
    coEvery { outputApi.getOutputs() } coAnswers {
      Either.left(IOException())
    }

    viewModel.reload()

    viewModel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.UnknownError)
    }
  }

  @Test
  fun `if the user changes the output the result should update the live data`() {
    coEvery { outputApi.setOutput(any()) } coAnswers {
      Either.right(
        OutputResponse(
          devices = listOf("Output 1", "Output 2"),
          active = "Output 2"
        )
      )
    }

    viewModel.setOutput("Output 2")

    viewModel.outputs.observeOnce {
      assertThat(it).containsExactlyElementsIn(listOf("Output 1", "Output 2"))
    }
    viewModel.selection.observeOnce {
      assertThat(it).isEqualTo("Output 2")
    }
    viewModel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.Success)
    }
  }
}