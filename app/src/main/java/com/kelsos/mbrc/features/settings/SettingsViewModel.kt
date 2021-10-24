package com.kelsos.mbrc.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingsActions(
  settings: SettingsManager,
  dispatchers: AppCoroutineDispatchers,
  scope: CoroutineScope
) {
  val setDebugLogging: (enabled: Boolean) -> Unit = {
    scope.launch(dispatchers.io) {
      settings.setDebugLogging(it)
    }
  }
  val setPluginUpdateCheck: (enabled: Boolean) -> Unit = {
    scope.launch(dispatchers.io) {
      settings.setPluginUpdateCheck(it)
    }
  }
  val setCallAction: (callAction: CallAction) -> Unit = {
    scope.launch(dispatchers.io) {
      settings.setCallAction(it)
    }
  }
  val setLibraryAction: (queue: Queue) -> Unit = {
    scope.launch(dispatchers.io) {
      settings.setLibraryAction(it)
    }
  }
}

class SettingsViewModel(
  settings: SettingsManager,
  dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  val state = settings.state
  val actions = SettingsActions(settings, dispatchers, viewModelScope)
}
