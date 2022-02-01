package com.kelsos.mbrc.features.minicontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.next
import com.kelsos.mbrc.networking.client.playPause
import com.kelsos.mbrc.networking.client.previous
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MiniControlViewModel(
  appState: AppState,
  private val userActionUseCase: UserActionUseCase
) : ViewModel() {
  val state: Flow<MiniControlState> = combine(
    appState.playingTrack,
    appState.playingPosition,
    appState.playerStatus.map { it.state }.distinctUntilChanged()
  ) { playingTrack, playingPosition, playerState ->
    MiniControlState(
      playingTrack = playingTrack,
      playingPosition = playingPosition,
      playingState = playerState
    )
  }

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

data class MiniControlState(
  val playingTrack: PlayingTrack = PlayingTrack(),
  val playingPosition: PlayingPosition = PlayingPosition(),
  val playingState: PlayerState = PlayerState.Undefined
)
