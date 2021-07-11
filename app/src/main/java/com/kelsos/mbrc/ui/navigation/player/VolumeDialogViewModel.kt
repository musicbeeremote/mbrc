package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class VolumeDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  appState: AppState
) : ViewModel() {
  val playerStatus: Flow<PlayerStatusModel> = appState.playerStatus
  private val volume: MutableSharedFlow<Int> = MutableSharedFlow()
  init {
    viewModelScope.launch {
      volume.sample(VOLUME_THROTTLE_MS).collect { volume ->
        userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
      }
    }
  }

  fun mute() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
    }
  }

  fun changeVolume(volume: Int) {
    viewModelScope.launch {
      this@VolumeDialogViewModel.volume.emit(volume)
    }
  }

  companion object {
    private const val VOLUME_THROTTLE_MS = 400L
  }
}
