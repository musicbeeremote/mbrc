package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import javax.inject.Inject

class PlayerPresenterImpl
@Inject
constructor(
  private val userActionUseCase: UserActionUseCase,
  private val settingsManager: SettingsManager,
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  trackRatingLiveDataProvider: TrackRatingLiveDataProvider,
  trackPositionLiveDataProvider: TrackPositionLiveDataProvider,
) : BasePresenter<PlayerView>(), PlayerPresenter {

  private val progressRelay: MutableSharedFlow<Int> = MutableStateFlow(0)
  private val volumeRelay: MutableSharedFlow<Int> = MutableStateFlow(0)

  init {
    playingTrackLiveDataProvider.observe(this) { activeTrack ->
      view().updateTrackInfo(activeTrack)
    }

    playerStatusLiveDataProvider.observe(this) { playerStatus ->
      view().updateStatus(playerStatus)
    }

    trackRatingLiveDataProvider.observe(this) { rating ->
      view().updateRating(rating)
    }

    trackPositionLiveDataProvider.observe(this) {
      view().updateProgress(it)
    }
  }

  override fun attach(view: PlayerView) {
    super.attach(view)
    progressRelay.sample(800).onEach { position ->
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
    }.launchIn(scope)
    volumeRelay.sample(800).onEach { volume ->
      userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
    }.launchIn(scope)
  }

  override fun stop(): Boolean {
    userActionUseCase.perform(UserAction(Protocol.PlayerStop, true))
    return true
  }

  override fun mute() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
  }

  override fun shuffle() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerShuffle))
  }

  override fun repeat() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerRepeat))
  }

  override fun changeVolume(value: Int) {
    volumeRelay.tryEmit(value)
  }

  override fun seek(position: Int) {
    progressRelay.tryEmit(position)
  }

  override fun load() {
    if (settingsManager.shouldShowChangeLog()) {
      view().showChangeLog()
    }
  }

  override fun toggleScrobbling() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerScrobble))
  }

  override fun play() {
    userActionUseCase.perform(UserAction(Protocol.PlayerPlayPause, true))
  }

  override fun previous() {
    userActionUseCase.perform(UserAction(Protocol.PlayerPrevious, true))
  }

  override fun next() {
    val action = UserAction(Protocol.PlayerNext, true)
    userActionUseCase.perform(action)
  }

  override fun lfmLove(): Boolean {
    userActionUseCase.perform(UserAction.toggle(Protocol.NowPlayingLfmRating))
    return true
  }
}
