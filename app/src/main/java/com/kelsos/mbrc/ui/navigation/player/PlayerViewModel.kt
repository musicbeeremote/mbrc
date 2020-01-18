package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.kelsos.mbrc.common.utilities.AppRxSchedulers
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import java.util.concurrent.TimeUnit

class PlayerViewModel(
  private val settingsManager: SettingsManager,
  private val userActionUseCase: UserActionUseCase,
  private val appRxSchedulers: AppRxSchedulers,
  val playingTrack: PlayingTrackState,
  val playerStatus: PlayerStatusState,
  val trackRating: TrackRatingState,
  val trackPosition: TrackPositionState
) : ViewModel() {

  private val progressRelay: PublishRelay<Int> = PublishRelay.create()
  private val disposable = progressRelay.throttleLast(
    800,
    TimeUnit.MILLISECONDS,
    appRxSchedulers.network
  )
    .subscribeOn(appRxSchedulers.network)
    .subscribe { position ->
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingPosition, position))
    }

  override fun onCleared() {
    disposable.dispose()
    super.onCleared()
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
    progressRelay.accept(position)
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