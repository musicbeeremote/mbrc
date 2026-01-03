package com.kelsos.mbrc.feature.library

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.mvvm.BaseViewModel
import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.queue.Queue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

open class BaseLibraryViewModel<T : UiMessageBase>(
  private val librarySettings: LibrarySettings,
  private val connectionStateFlow: ConnectionStateFlow
) : BaseViewModel<T>() {
  protected suspend fun getQueueAction(action: Queue): Queue = if (action == Queue.Default) {
    val trackAction = librarySettings.libraryTrackDefaultActionFlow.first()
    Queue.fromTrackAction(trackAction)
  } else {
    action
  }

  protected fun launchDefault(event: T) {
    viewModelScope.launch {
      emit(event)
    }
  }

  protected suspend fun checkConnection(): Boolean = connectionStateFlow.isConnected
}
