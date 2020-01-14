package com.kelsos.mbrc.ui.dialogs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputResponse
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.observeOnce
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
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
    outputApi = mockk()

    viewmodel = OutputSelectionViewModel(
      outputApi = outputApi,
      dispatchers = TestDispatchers.dispatchers
    )
  }

  @Test
  fun `originally view model should have empty values`() {
    viewmodel.outputs.observeOnce {
      assertThat(it).isEmpty()
    }
    viewmodel.selection.observeOnce {
      assertThat(it).isEmpty()
    }
  }

  @Test
  fun `after reload it should return the output information`() {
    every { outputApi.getOutputs() } answers {
      Single.just(
        OutputResponse(
          devices = listOf("Output 1", "Output 2"),
          active = "Output 2"
        )
      )
    }

    viewmodel.reload()
    viewmodel.outputs.observeOnce {
      assertThat(it).containsAllIn(listOf("Output 1", "Output 2"))
    }
    viewmodel.selection.observeOnce {
      assertThat(it).isEqualTo("Output 2")
    }
    viewmodel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.Success)
    }
  }

  @Test
  fun `if there is a socket timeout the emitter should have the proper result`() {
    every { outputApi.getOutputs() } answers {
      Single.error(SocketTimeoutException())
    }

    viewmodel.reload()

    viewmodel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.ConnectionError)
    }
  }

  @Test
  fun `if there is a socket exception the emitter should have the proper result`() {
    every { outputApi.getOutputs() } answers {
      Single.error(SocketException())
    }

    viewmodel.reload()

    viewmodel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.ConnectionError)
    }
  }

  @Test
  fun `if there is an exception the emitter should have the proper result`() {
    every { outputApi.getOutputs() } answers {
      Single.error(IOException())
    }

    viewmodel.reload()

    viewmodel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.UnknownError)
    }
  }

  @Test
  fun `if the user changes the output the result should update the live data`() {
    every { outputApi.setOutput(any()) } answers {
      Single.just(
        OutputResponse(
          devices = listOf("Output 1", "Output 2"),
          active = "Output 2"
        )
      )
    }

    viewmodel.setOutput("Output 2")

    viewmodel.outputs.observeOnce {
      assertThat(it).containsAllIn(listOf("Output 1", "Output 2"))
    }
    viewmodel.selection.observeOnce {
      assertThat(it).isEqualTo("Output 2")
    }
    viewmodel.emitter.observeOnce {
      assertThat(it.peekContent()).isEqualTo(OutputSelectionResult.Success)
    }
  }
}