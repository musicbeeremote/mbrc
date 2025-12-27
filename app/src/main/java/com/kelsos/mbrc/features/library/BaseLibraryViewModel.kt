package com.kelsos.mbrc.features.library

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.mvvm.BaseViewModel
import com.kelsos.mbrc.common.mvvm.UiMessageBase
import com.kelsos.mbrc.common.state.ConnectionStateFlow
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.settings.SettingsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

open class BaseLibraryViewModel<T : UiMessageBase>(
  private val settingsManager: SettingsManager,
  private val connectionStateFlow: ConnectionStateFlow
) : BaseViewModel<T>() {
  protected suspend fun getQueueAction(action: Queue): Queue = if (action == Queue.Default) {
    val trackAction = settingsManager.libraryTrackDefaultActionFlow.first()
    Queue.fromTrackAction(trackAction)
  } else {
    action
  }

  protected fun launchDefault(event: T) {
    viewModelScope.launch {
      emit(event)
    }
  }

  protected suspend fun checkConnection(): Boolean = connectionStateFlow.isConnected()
}
