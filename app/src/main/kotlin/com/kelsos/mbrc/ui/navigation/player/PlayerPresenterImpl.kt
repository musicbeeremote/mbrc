package com.kelsos.mbrc.ui.navigation.player

import com.jakewharton.rxrelay2.PublishRelay
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerPresenterImpl
@Inject
constructor(
  private val settingsManager: SettingsManager,
  private val userActionUseCase: UserActionUseCase,
  private val appRxSchedulers: AppRxSchedulers,
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  trackRatingLiveDataProvider: TrackRatingLiveDataProvider,
  trackPositionLiveDataProvider: TrackPositionLiveDataProvider
) : BasePresenter<PlayerView>(), PlayerPresenter {

  private val progressRelay: PublishRelay<Int> = PublishRelay.create()

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

  override fun showVolumeDialog() {
    view().showVolumeDialog()
  }

  override fun attach(view: PlayerView) {
    super.attach(view)
    disposables += progressRelay.throttleLast(
      800,
      TimeUnit.MILLISECONDS,
      appRxSchedulers.network
    )
      .subscribeOn(appRxSchedulers.network)
      .subscribe { position ->
        userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
      }
  }

  override fun stop(): Boolean {
    userActionUseCase.perform(UserAction(Protocol.PlayerStop, true))
    return true
  }

  override fun shuffle() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerShuffle))
  }

  override fun repeat() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerRepeat))
  }

  override fun seek(position: Int) {
    progressRelay.accept(position)
  }

  override fun load() {
    disposables += settingsManager.shouldShowChangeLog().subscribe({
      if (it) {
        view().showChangeLog()
      }
    }) {
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