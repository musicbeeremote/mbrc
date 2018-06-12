package com.kelsos.mbrc.ui.navigation.main

import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import javax.inject.Inject

class MainViewPresenterImpl
@Inject
constructor(
  private val userActionUseCase: UserActionUseCase,
  connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider,
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  trackRatingLiveDataProvider: TrackRatingLiveDataProvider,
  trackPositionLiveDataProvider: TrackPositionLiveDataProvider
) : BasePresenter<MainView>(), MainViewPresenter {

  init {
    playingTrackLiveDataProvider.get().observe(this) { activeTrack ->
      if (activeTrack == null) {
        return@observe
      }
      view().updateTrackInfo(activeTrack)
    }

    playerStatusLiveDataProvider.get().observe(this) { playerStatus ->
      if (playerStatus == null) {
        return@observe
      }
      view().updateStatus(playerStatus)
    }

    trackRatingLiveDataProvider.get().observe(this) { rating ->
      if (rating == null) {
        return@observe
      }
      view().updateRating(rating)
    }

    connectionStatusLiveDataProvider.get().observe(this) { status ->
      if (status == null) {
        return@observe
      }
      view().updateConnection(status.status)
    }

    trackPositionLiveDataProvider.get().observe(this) {
      if (it == null) {
        return@observe
      }

      view().updateProgress(it)
    }
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
    userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, value))
  }

  override fun seek(position: Int) {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
  }

  override fun requestNowPlayingPosition() {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition))
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
