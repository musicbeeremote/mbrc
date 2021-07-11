package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PlayerViewModel(
  settingsManager: SettingsManager,
  appState: AppState,
  private val userActionUseCase: UserActionUseCase
) : BaseViewModel<PlayerUiMessage>() {
  private val progressRelay: MutableSharedFlow<Int> = MutableStateFlow(0)
  val playingTrack: Flow<PlayingTrack> = appState.playingTrack
  val playerStatus: Flow<PlayerStatusModel> = appState.playerStatus
  val playingTrackRating: Flow<TrackRating> = appState.playingTrackRating
  val playingPosition: Flow<PlayingPosition> = appState.playingPosition

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

  fun stop(): Boolean {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerStop, true))
    }
    return true
  }

  fun shuffle() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerShuffle))
    }
  }

  fun repeat() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerRepeat))
    }
  }

  fun seek(position: Int) {
    viewModelScope.launch {
      progressRelay.emit(position)
    }
  }

  fun toggleScrobbling() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.PlayerScrobble))
    }
  }

  fun play() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerPlayPause, true))
    }
  }

  fun previous() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerPrevious, true))
    }
  }

  fun next() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction(Protocol.PlayerNext, true))
    }
  }

  fun favorite(): Boolean {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.toggle(Protocol.NowPlayingLfmRating))
    }
    return true
  }

  companion object {
    private const val SAMPLE_PERIOD_MS = 800L
  }
}
