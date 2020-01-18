package com.kelsos.mbrc.features.output

import androidx.lifecycle.viewModelScope
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.net.SocketException
import java.net.SocketTimeoutException

class OutputSelectionViewModel(
  private val outputApi: OutputApi,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<OutputSelectionResult>() {

  private val _outputs: MutableSharedFlow<OutputResponse> = MutableSharedFlow()

  val outputs: SharedFlow<OutputResponse>
    get() = _outputs

  private fun code(throwable: Throwable?): OutputSelectionResult {
    return when (throwable?.cause ?: throwable) {
      is SocketException -> OutputSelectionResult.ConnectionError
      is SocketTimeoutException -> OutputSelectionResult.ConnectionError
      else -> OutputSelectionResult.UnknownError
    }
  }

  private fun Try<OutputResponse>.toResult(): OutputSelectionResult {
    return toEither().fold({ code(it) }, { OutputSelectionResult.Success })
  }

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      val result = Try {
        outputApi.getOutputs().also {
          _outputs.emit(it)
        }
      }.toResult()
      emit(result)
    }
  }

  fun setOutput(output: String) {
    viewModelScope.launch(dispatchers.network) {
      val result = Try {
        outputApi.setOutput(output).also {
          _outputs.emit(it)
        }
      }.toResult()
      emit(result)
    }
  }
}
