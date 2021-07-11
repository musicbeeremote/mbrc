package com.kelsos.mbrc.features.minicontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.next
import com.kelsos.mbrc.networking.client.playPause
import com.kelsos.mbrc.networking.client.previous
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MiniControlViewModel(
  appState: AppState,
  private val userActionUseCase: UserActionUseCase
) : ViewModel() {
  val playingTrack: Flow<PlayingTrack> = appState.playingTrack
  val playerStatus: Flow<PlayerStatusModel> = appState.playerStatus
  val playingPosition: Flow<PlayingPosition> = appState.playingPosition

  fun next() {
    viewModelScope.launch { userActionUseCase.next() }
  }

  fun previous() {
    viewModelScope.launch { userActionUseCase.previous() }
  }

  fun playPause() {
    viewModelScope.launch { userActionUseCase.playPause() }
  }
}
