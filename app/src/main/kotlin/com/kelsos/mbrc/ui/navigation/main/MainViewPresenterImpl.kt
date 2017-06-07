package com.kelsos.mbrc.ui.navigation.main

import com.kelsos.mbrc.content.active_status.MainDataModel
import com.kelsos.mbrc.events.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.CoverChangedEvent
import com.kelsos.mbrc.events.LfmRatingChanged
import com.kelsos.mbrc.events.PlayStateChange
import com.kelsos.mbrc.events.RepeatChange
import com.kelsos.mbrc.events.ScrobbleChange
import com.kelsos.mbrc.events.ShuffleChange
import com.kelsos.mbrc.events.TrackInfoChangeEvent
import com.kelsos.mbrc.events.UpdatePositionEvent
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.VolumeChange
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.connections.ConnectionStatusModel
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class MainViewPresenterImpl
@Inject constructor(val bus: RxBus,
                    val model: MainDataModel,
                    val connectionStatusModel: ConnectionStatusModel,
                    private val settingsManager: SettingsManager) : BasePresenter<MainView>(), MainViewPresenter {
  override fun stop(): Boolean {
    bus.post(UserAction(Protocol.PlayerStop, true))
    return true
  }

  override fun mute() {
    bus.post(UserAction.toggle(Protocol.PlayerMute))
  }

  override fun shuffle() {
    bus.post(UserAction.toggle(Protocol.PlayerShuffle))
  }

  override fun repeat() {
    bus.post(UserAction.toggle(Protocol.PlayerRepeat))
  }

  override fun changeVolume(value: Int) {
    bus.post(UserAction.create(Protocol.PlayerVolume, value))
  }

  override fun seek(position: Int) {
    bus.post(UserAction.create(Protocol.NowPlayingPosition, position))
  }

  override fun load() {
    addDisposable(Completable.fromCallable {
      checkIfAttached()
      view?.updateCover(model.coverPath)
      view?.updateLfmStatus(model.lfmStatus)
      view?.updateScrobbleStatus(model.isScrobblingEnabled)
      view?.updateRepeat(model.repeat)
      view?.updateShuffleState(model.shuffle)
      view?.updateVolume(model.volume, model.isMute)
      view?.updatePlayState(model.playState)
      view?.updateTrackInfo(model.trackInfo)
      view?.updateConnection(connectionStatusModel.connection)
    }.subscribe({

    }) {
      Timber.e(it, "Failed to load")
    })

    addDisposable(settingsManager.shouldShowChangeLog().subscribe({
      if (it) {
        view?.showChangeLog()
      }
    }) {

    })


  }

  override fun requestNowPlayingPosition() {
    bus.post(UserAction.create(Protocol.NowPlayingPosition))
  }

  override fun toggleScrobbling() {
    bus.post(UserAction.toggle(Protocol.PlayerScrobble))
  }

  override fun attach(view: MainView) {
    super.attach(view)
    this.bus.register(this, CoverChangedEvent::class.java, { this.view?.updateCover(it.path) }, true)
    this.bus.register(this, ShuffleChange::class.java, { this.view?.updateShuffleState(it.shuffleState) }, true)
    this.bus.register(this, RepeatChange::class.java, { this.view?.updateRepeat(it.mode) }, true)
    this.bus.register(this, VolumeChange::class.java, { this.view?.updateVolume(it.volume, it.isMute) }, true)
    this.bus.register(this, PlayStateChange::class.java, { this.view?.updatePlayState(it.state) }, true)
    this.bus.register(this, TrackInfoChangeEvent::class.java, { this.view?.updateTrackInfo(it.trackInfo) }, true)
    this.bus.register(this, ConnectionStatusChangeEvent::class.java, { this.view?.updateConnection(it.status) }, true)
    this.bus.register(this, UpdatePositionEvent::class.java, { this.view?.updateProgress(it) }, true)
    this.bus.register(this, ScrobbleChange::class.java, { this.view?.updateScrobbleStatus(it.isActive) }, true)
    this.bus.register(this, LfmRatingChanged::class.java, { this.view?.updateLfmStatus(it.status) }, true)
    model.setOnPluginOutOfDate { this.view?.notifyPluginOutOfDate() }
  }

  override fun detach() {
    super.detach()
    this.bus.unregister(this)
    model.setOnPluginOutOfDate(null)
  }

  override fun play() {
    bus.post(UserAction(Protocol.PlayerPlayPause, true))
  }

  override fun previous() {
    bus.post(UserAction(Protocol.PlayerPrevious, true))
  }

  override fun next() {
    val action = UserAction(Protocol.PlayerNext, true)
    bus.post(action)
  }

  override fun lfmLove(): Boolean {
    bus.post(UserAction.toggle(Protocol.NowPlayingLfmRating))
    return true
  }


}
