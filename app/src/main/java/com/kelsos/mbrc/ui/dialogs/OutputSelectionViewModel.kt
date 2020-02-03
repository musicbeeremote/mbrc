package com.kelsos.mbrc.ui.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.content.output.OutputResponse
import com.kelsos.mbrc.ui.BaseViewModel
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class OutputSelectionViewModel(
  private val outputApi: OutputApi,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<OutputSelectionResult>(dispatchers) {

  private val _outputs: MutableLiveData<List<String>> = MutableLiveData()
  private val _selection: MutableLiveData<String> = MutableLiveData()

  init {
      _outputs.postValue(emptyList())
      _selection.postValue("")
  }

  val outputs: LiveData<List<String>>
    get() = _outputs

  val selection: LiveData<String>
    get() = _selection

  private fun updateState(response: OutputResponse) {
    _outputs.postValue(response.devices)
    _selection.postValue(response.active)
  }

  private fun code(throwable: Throwable?): OutputSelectionResult {
    val cause = throwable?.cause ?: throwable

    return when (cause) {
      is SocketException -> OutputSelectionResult.ConnectionError
      is SocketTimeoutException -> OutputSelectionResult.ConnectionError
      else -> OutputSelectionResult.UnknownError
    }
  }

  private fun Try<OutputResponse>.toResult(): OutputSelectionResult {
    return toEither().fold({ code(it) }, { OutputSelectionResult.Success })
  }

  fun reload() {
    scope.launch {
      val result = Try {
        outputApi.getOutputs().await().also {
          updateState(it)
        }
      }.toResult()
      emit(result)
    }
  }

  fun setOutput(output: String) {
    scope.launch {
      val result = Try {
        outputApi.setOutput(output).await().also {
          updateState(it)
        }
      }.toResult()
      emit(result)
    }
  }
}