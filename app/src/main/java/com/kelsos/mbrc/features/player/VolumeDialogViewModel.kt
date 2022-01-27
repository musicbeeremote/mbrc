package com.kelsos.mbrc.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class VolumeDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  appState: AppState,
) : ViewModel() {
  private val _currentVolume: MutableStateFlow<Int> = MutableStateFlow(0)
  private val _muted: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val currentVolume: Flow<Int> get() = _currentVolume
  val muted: Flow<Boolean> get() = _muted

  private val volume: MutableSharedFlow<Int> = MutableSharedFlow()

  init {
    viewModelScope.launch {
      appState.playerStatus.map { it.volume }.distinctUntilChanged().collect { volume ->
        _currentVolume.emit(volume)
      }
    }
    viewModelScope.launch {
      appState.playerStatus.map { it.mute }.distinctUntilChanged().collect {
        _muted.emit(it)
      }
    }
    viewModelScope.launch {
      volume.collect { volume ->
        _currentVolume.emit(volume)
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
}
