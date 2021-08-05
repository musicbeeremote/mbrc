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

  fun perform(action: MiniControlAction) {
    viewModelScope.launch {
      when (action) {
        MiniControlAction.PlayNext -> userActionUseCase.next()
        MiniControlAction.PlayPause -> userActionUseCase.playPause()
        MiniControlAction.PlayPrevious -> userActionUseCase.previous()
      }
    }
  }
}
