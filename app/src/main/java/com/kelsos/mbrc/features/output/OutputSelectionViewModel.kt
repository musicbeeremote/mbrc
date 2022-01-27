package com.kelsos.mbrc.features.output

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.net.SocketException
import java.net.SocketTimeoutException

class OutputSelectionViewModel(
  private val outputApi: OutputApi,
  private val dispatchers: AppCoroutineDispatchers,
) : BaseViewModel<OutputSelectionResult>() {
  private val _outputs: MutableSharedFlow<OutputResponse> = MutableSharedFlow()

  val outputs: SharedFlow<OutputResponse>
    get() = _outputs

  private fun code(throwable: Throwable?): OutputSelectionResult =
    when (throwable?.cause ?: throwable) {
      is SocketException -> OutputSelectionResult.ConnectionError
      is SocketTimeoutException -> OutputSelectionResult.ConnectionError
      else -> OutputSelectionResult.UnknownError
    }

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      val result =
        outputApi
          .getOutputs()
          .fold(
            {
              code(it)
            },
            {
              _outputs.emit(it)
              OutputSelectionResult.Success
            },
          )
      emit(result)
    }
  }

  fun setOutput(output: String) {
    viewModelScope.launch(dispatchers.network) {
      val result =
        outputApi
          .setOutput(output)
          .fold(
            {
              code(it)
            },
            {
              _outputs.emit(it)
              OutputSelectionResult.Success
            },
          )
      emit(result)
    }
  }
}
