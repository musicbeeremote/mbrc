package com.kelsos.mbrc.feature.minicontrol

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.mvvm.BaseViewModel
import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.next
import com.kelsos.mbrc.core.networking.protocol.usecases.playPause
import com.kelsos.mbrc.core.networking.protocol.usecases.previous
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class MiniControlUiMessages : UiMessageBase {
  data object NetworkUnavailable : MiniControlUiMessages()

  data object ActionFailed : MiniControlUiMessages()
}

class MiniControlViewModel(
  appState: AppStateFlow,
  private val userActionUseCase: UserActionUseCase,
  private val connectionStateFlow: ConnectionStateFlow,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<MiniControlUiMessages>() {
  val state: Flow<MiniControlState> =
    combine(
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
    viewModelScope.launch(dispatchers.network) {
      if (!connectionStateFlow.isConnected) {
        emit(MiniControlUiMessages.NetworkUnavailable)
        return@launch
      }
      try {
        when (action) {
          MiniControlAction.PlayNext -> userActionUseCase.next()
          MiniControlAction.PlayPause -> userActionUseCase.playPause()
          MiniControlAction.PlayPrevious -> userActionUseCase.previous()
        }
      } catch (e: IOException) {
        Timber.e(e)
        emit(MiniControlUiMessages.ActionFailed)
      }
    }
  }
}

@Stable
data class MiniControlState(
  val playingTrack: TrackInfo = BasicTrackInfo(),
  val playingPosition: PlayingPosition = PlayingPosition(),
  val playingState: PlayerState = PlayerState.Undefined
)
