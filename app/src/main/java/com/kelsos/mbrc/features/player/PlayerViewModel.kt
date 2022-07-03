package com.kelsos.mbrc.features.player

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.settings.SettingsManager
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PlayerViewModel(
  settingsManager: SettingsManager,
  appState: AppState,
  private val userActionUseCase: UserActionUseCase
) : BaseViewModel<PlayerUiMessage>() {
  private val progressRelay: MutableSharedFlow<Int> = MutableStateFlow(0)
  val state: Flow<PlayerStateModel> = combine(
    appState.playingTrack,
    appState.playerStatus,
    appState.playingTrackRating,
    appState.playingPosition
  ) { playingTrack, playerStatus, trackRating, playingPosition ->
    PlayerStateModel(
      playingTrack = playingTrack,
      playerStatus = playerStatus,
      trackRating = trackRating,
      playingPosition = playingPosition
    )
  }

  init {
    viewModelScope.launch {
      progressRelay.sample(SAMPLE_PERIOD_MS).collect { position ->
        userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
      }
    }

    viewModelScope.launch {
      if (settingsManager.shouldShowChangeLog()) {
        emit(PlayerUiMessage.ShowChangelog)
      }
    }
  }

  fun interact(action: PlayerAction) {
    if (action is PlayerAction.Seek) {
      viewModelScope.launch {
        progressRelay.emit(action.position)
      }
      return
    }

    val userAction = when (action) {
      PlayerAction.ToggleFavorite -> UserAction.toggle(Protocol.NowPlayingLfmRating)
      PlayerAction.PlayNext -> UserAction(Protocol.PlayerNext, true)
      PlayerAction.ResumePlayOrPause -> UserAction(Protocol.PlayerPlayPause, true)
      PlayerAction.PlayPrevious -> UserAction(Protocol.PlayerPrevious, true)
      PlayerAction.ToggleRepeat -> UserAction.toggle(Protocol.PlayerRepeat)
      PlayerAction.ToggleScrobbling -> UserAction.toggle(Protocol.PlayerScrobble)
      is PlayerAction.Seek -> throw IllegalArgumentException("Handled before")
      PlayerAction.ToggleShuffle -> UserAction.toggle(Protocol.PlayerShuffle)
      PlayerAction.Stop -> UserAction(Protocol.PlayerStop, true)
    }
    viewModelScope.launch {
      userActionUseCase.perform(userAction)
    }
  }

  companion object {
    private const val SAMPLE_PERIOD_MS = 800L
  }
}

data class PlayerStateModel(
  val playingTrack: PlayingTrack = PlayingTrack(),
  val playingPosition: PlayingPosition = PlayingPosition(),
  val playerStatus: PlayerStatusModel = PlayerStatusModel(),
  val trackRating: TrackRating = TrackRating()
)
