package com.kelsos.mbrc.features.player

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.mvvm.BaseViewModel
import com.kelsos.mbrc.common.state.AppStateFlow
import com.kelsos.mbrc.common.state.PlayingPosition
import com.kelsos.mbrc.common.state.PlayingTrack
import com.kelsos.mbrc.common.state.TrackRating
import com.kelsos.mbrc.features.settings.SettingsManager
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.performUserAction
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PlayerViewModel(
  settingsManager: SettingsManager,
  appState: AppStateFlow,
  private val userActionUseCase: UserActionUseCase
) : BaseViewModel<PlayerUiMessage>() {
  private val progressRelay: MutableSharedFlow<Int> = MutableSharedFlow()
  private val volumeRelay: MutableSharedFlow<Int> = MutableSharedFlow()

  // Separate flows for granular recomposition
  val playingTrack: StateFlow<PlayingTrack> = appState.playingTrack
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayingTrack())

  val playingPosition: StateFlow<PlayingPosition> = appState.playingPosition
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayingPosition())

  val trackRating: StateFlow<TrackRating> = appState.playingTrackRating
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackRating())

  val volumeState: StateFlow<VolumeState> = appState.playerStatus
    .map { VolumeState(volume = it.volume, mute = it.mute) }
    .distinctUntilChanged()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VolumeState())

  val playbackState: StateFlow<PlaybackState> = appState.playerStatus
    .map { PlaybackState(playerState = it.state, shuffle = it.shuffle, repeat = it.repeat) }
    .distinctUntilChanged()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlaybackState())

  val isScrobbling: StateFlow<Boolean> = appState.playerStatus
    .map { it.scrobbling }
    .distinctUntilChanged()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val actions: IPlayerActions = PlayerActions(
    userActionUseCase = userActionUseCase,
    scope = viewModelScope,
    progressRelay = progressRelay,
    volumeRelay = volumeRelay
  )

  init {
    viewModelScope.launch {
      progressRelay.sample(ACTION_DEBOUNCE_MS).collect { position ->
        userActionUseCase.performUserAction(Protocol.NowPlayingPosition, position)
      }
    }

    viewModelScope.launch {
      volumeRelay.sample(ACTION_DEBOUNCE_MS).collect { volume ->
        userActionUseCase.performUserAction(Protocol.PlayerVolume, volume)
      }
    }

    viewModelScope.launch {
      if (settingsManager.checkShouldShowChangeLog()) {
        emit(PlayerUiMessage.ShowChangelog)
      }
    }
  }

  companion object {
    private const val ACTION_DEBOUNCE_MS = 800L
  }
}
