package com.kelsos.mbrc.feature.playback.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.TrackDetails
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.performUserAction
import com.kelsos.mbrc.core.networking.protocol.usecases.playPause
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LyricsViewModel(appState: AppStateFlow, private val userActionUseCase: UserActionUseCase) :
  ViewModel() {
  val lyrics: Flow<List<String>> = appState.lyrics
  val playingTrack: StateFlow<TrackInfo> = appState.playingTrack
  val playingPosition: StateFlow<PlayingPosition> = appState.playingPosition
  val trackDetails: StateFlow<TrackDetails> = appState.playingTrackDetails
  val isPlaying: StateFlow<Boolean> = appState.playerStatus
    .map { it.state == PlayerState.Playing }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  fun playPause() {
    viewModelScope.launch {
      userActionUseCase.playPause()
    }
  }

  fun seek(position: Int) {
    viewModelScope.launch {
      userActionUseCase.performUserAction(Protocol.NowPlayingPosition, position)
    }
  }
}
