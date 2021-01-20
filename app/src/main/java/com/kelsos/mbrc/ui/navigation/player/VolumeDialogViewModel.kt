package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach

@FlowPreview
class VolumeDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  val playerStatus: PlayerStatusState
) : ViewModel() {

  private val volumeFlow: MutableStateFlow<Int> = MutableStateFlow(0)

  init {
    volumeFlow.debounce(800).onEach { volume ->
      userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
    }
  }

  fun mute() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
  }

  fun changeVolume(volume: Int) {
    volumeFlow.tryEmit(volume)
  }
}
