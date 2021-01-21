package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
class PlayerViewModel(
  settingsManager: SettingsManager,
  dispatchers: AppDispatchers,
  private val userActionUseCase: UserActionUseCase,
  val playingTrack: PlayingTrackState,
  val playerStatus: PlayerStatusState,
  val trackRating: TrackRatingState,
  val trackPosition: TrackPositionState
) : BaseViewModel<PlayerUiMessage>(dispatchers) {

  private val stateFlow: MutableStateFlow<Int> = MutableStateFlow(0)

  init {
    stateFlow.debounce(800).onEach { position ->
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
    }.launchIn(scope)
    if (settingsManager.shouldShowChangeLog()) {
      emit(PlayerUiMessage.ShowChangelog)
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
    stateFlow.tryEmit(position)
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
