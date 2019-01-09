package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PlayerViewModel(
  private val userActionUseCase: UserActionUseCase,
  val playingTrack: PlayingTrackLiveDataProvider,
  val playerStatus: PlayerStatusLiveDataProvider,
  val trackRating: TrackRatingLiveDataProvider,
  val trackPosition: TrackPositionLiveDataProvider,
) : ViewModel() {
  private val progressRelay: MutableSharedFlow<Int> = MutableStateFlow(0)
  init {
    viewModelScope.launch {
      progressRelay.sample(800).collect { position ->
        userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
      }
    }
  }

  fun stop(): Boolean {
    userActionUseCase.perform(UserAction(Protocol.PlayerStop, true))
    return true
  }

  fun shuffle() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerShuffle))
  }

  fun repeat() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerRepeat))
  }

  fun seek(position: Int) {
    progressRelay.tryEmit(position)
  }

  fun toggleScrobbling() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerScrobble))
  }

  fun play() {
    userActionUseCase.perform(UserAction(Protocol.PlayerPlayPause, true))
  }

  fun previous() {
    userActionUseCase.perform(UserAction(Protocol.PlayerPrevious, true))
  }

  fun next() {
    val action = UserAction(Protocol.PlayerNext, true)
    userActionUseCase.perform(action)
  }

  fun favorite(): Boolean {
    userActionUseCase.perform(UserAction.toggle(Protocol.NowPlayingLfmRating))
    return true
  }
}
