package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class VolumeDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  val playerStatus: PlayerStatusLiveDataProvider
) : ViewModel() {

  private val volumeFlow: MutableStateFlow<Int> = MutableStateFlow(0)
  init {
    viewModelScope.launch {
      volumeFlow.sample(800).collect { volume ->
        userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
      }
    }
  }

  fun mute() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
  }

  fun changeVolume(volume: Int) {
    volumeFlow.tryEmit(volume)
  }
}
