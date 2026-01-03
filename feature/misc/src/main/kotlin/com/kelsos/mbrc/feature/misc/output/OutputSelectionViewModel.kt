package com.kelsos.mbrc.feature.misc.output

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.api.OutputApi
import com.kelsos.mbrc.core.networking.dto.OutputResponse
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okio.IOException
import timber.log.Timber

class OutputSelectionViewModel(
  private val outputApi: OutputApi,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  private val backingOutputs = MutableSharedFlow<OutputResponse>()
  private val backingEvents = MutableSharedFlow<Outcome<Unit>>()

  val outputs: SharedFlow<OutputResponse> = backingOutputs
  val events: SharedFlow<Outcome<Unit>> = backingEvents

  private fun mapError(throwable: Throwable?): Outcome<Unit> =
    when (throwable?.cause ?: throwable) {
      is SocketException -> Outcome.Failure(AppError.ConnectionRefused)
      is SocketTimeoutException -> Outcome.Failure(AppError.NetworkTimeout)
      else -> Outcome.Failure(AppError.Unknown(throwable))
    }

  fun reload() {
    Timber.d("call reload outputs")
    viewModelScope.launch(dispatchers.network) {
      Timber.d("reload outputs")
      try {
        backingOutputs.emit(outputApi.getOutputs())
      } catch (e: IOException) {
        backingEvents.emit(mapError(e))
        Timber.e(e)
      }
    }
  }

  fun setOutput(output: String) {
    viewModelScope.launch {
      try {
        backingOutputs.emit(outputApi.setOutput(output))
      } catch (e: IOException) {
        backingEvents.emit(mapError(e))
      }
    }
  }
}
