package com.kelsos.mbrc.feature.library

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.mvvm.BaseViewModel
import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.common.utilities.asFailure
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

open class BaseLibraryViewModel<T : UiMessageBase>(
  private val connectionStateFlow: ConnectionStateFlow
) : BaseViewModel<T>() {
  private val backingQueueResults = MutableSharedFlow<Outcome<Int>>(
    extraBufferCapacity = 64,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
  )

  /**
   * How each request made through [launchQueue] ended, as the number of tracks queued or the
   * reason it failed. Screens hand it to `QueueResultEffect`.
   */
  val queueResults: Flow<Outcome<Int>> = backingQueueResults

  protected fun launchDefault(event: T) {
    viewModelScope.launch {
      emit(event)
    }
  }

  /**
   * Runs [request] if MusicBee is reachable and reports the result on [queueResults]. Any failure
   * is reported as [AppError.OperationFailed].
   */
  protected fun launchQueue(request: suspend () -> Outcome<Int>) {
    viewModelScope.launch {
      if (!connectionStateFlow.isConnected) {
        backingQueueResults.emit(AppError.NetworkUnavailable.asFailure())
        return@launch
      }

      val result = request()
      backingQueueResults.emit(
        if (result is Outcome.Failure) AppError.OperationFailed.asFailure() else result
      )
    }
  }
}
