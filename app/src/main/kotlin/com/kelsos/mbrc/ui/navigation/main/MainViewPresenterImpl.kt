package com.kelsos.mbrc.ui.navigation.main

import androidx.lifecycle.Observer
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class MainViewPresenterImpl
@Inject
constructor(
  private val settingsManager: SettingsManager,
  private val userActionUseCase: UserActionUseCase,
  connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider,
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  trackRatingLiveDataProvider: TrackRatingLiveDataProvider,
  trackPositionLiveDataProvider: TrackPositionLiveDataProvider
) : BasePresenter<MainView>(), MainViewPresenter {

  init {
    playingTrackLiveDataProvider.get().observe(this, Observer { activeTrack ->
      if (activeTrack == null) {
        return@Observer
      }
      view().updateTrackInfo(activeTrack)
    })

    playerStatusLiveDataProvider.get().observe(this, Observer { playerStatus ->
      if (playerStatus == null) {
        return@Observer
      }
      view().updateStatus(playerStatus)
    })

    trackRatingLiveDataProvider.get().observe(this, Observer { rating ->
      if (rating == null) {
        return@Observer
      }
      view().updateRating(rating)
    })

    connectionStatusLiveDataProvider.get().observe(this, Observer { status ->
      if (status == null) {
        return@Observer
      }
      view().updateConnection(status.status)
    })

    trackPositionLiveDataProvider.get().observe(this, Observer {
      if (it == null) {
        return@Observer
      }

      view().updateProgress(it)
    })
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

  override fun load() {
    disposables += settingsManager.shouldShowChangeLog().subscribe({
      if (it) {
        view().showChangeLog()
      }
    }) {
    }
  }

  override fun requestNowPlayingPosition() {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition))
  }

  override fun toggleScrobbling() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerScrobble))
  }

  override fun attach(view: MainView) {
    super.attach(view)
//    bus.register(this, TrackPositionData::class.java, {
//      view().updateProgress(it)
//    }, true)
    //model.setOnPluginOutOfDate { view().notifyPluginOutOfDate() }
  }

  override fun detach() {
    super.detach()
    //model.setOnPluginOutOfDate(null)
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