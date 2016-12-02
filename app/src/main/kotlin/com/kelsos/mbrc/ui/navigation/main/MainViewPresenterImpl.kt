package com.kelsos.mbrc.ui.navigation.main

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.LfmRatingChanged
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.RepeatChange
import com.kelsos.mbrc.events.ui.ScrobbleChange
import com.kelsos.mbrc.events.ui.ShuffleChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.events.ui.UpdatePosition
import com.kelsos.mbrc.events.ui.VolumeChange
import com.kelsos.mbrc.model.ConnectionModel
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SettingsManager
import rx.Completable
import timber.log.Timber
import javax.inject.Inject

class MainViewPresenterImpl
@Inject constructor(val bus: RxBus,
                    val model: MainDataModel,
                    val connectionModel: ConnectionModel,
                    private val settingsManager: SettingsManager) : BasePresenter<MainView>(), MainViewPresenter {
  override fun stop(): Boolean {
    //todo api update
    return true
  }

  override fun mute() {
    //todo api update
  }

  override fun shuffle() {
    //todo api update
  }

  override fun repeat() {
    //todo api update
  }

  override fun changeVolume(value: Int) {
    //todo api update
  }

  override fun seek(position: Int) {
    //todo api update
  }

  override fun load() {
    addSubcription(Completable.fromCallable {
      checkIfAttached()
      view?.updateCover(model.coverPath)
      view?.updateLfmStatus(model.lfmStatus)
      view?.updateScrobbleStatus(model.isScrobblingEnabled)
      view?.updateRepeat(model.repeat)
      view?.updateShuffleState(model.shuffle)
      view?.updateVolume(model.volume, model.isMute)
      view?.updatePlayState(model.playState)
      view?.updateTrackInfo(model.trackInfo)
      view?.updateConnection(connectionModel.connection)
    }.subscribe({

    }) {
      Timber.e(it, "Failed to load")
    })

    addSubcription(settingsManager.shouldShowPluginUpdate().subscribe({
      if (it) {
        view?.showPluginUpdateDialog()
      }
    }) {

    })


  }

  override fun requestNowPlayingPosition() {
    //todo api update
  }

  override fun toggleScrobbling() {
    //todo api update
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
    this.bus.register(this, UpdatePosition::class.java, { this.view?.updateProgress(it) }, true)
    this.bus.register(this, ScrobbleChange::class.java, { this.view?.updateScrobbleStatus(it.isActive) }, true)
    this.bus.register(this, LfmRatingChanged::class.java, { this.view?.updateLfmStatus(it.status) }, true)
  }

  override fun detach() {
    super.detach()
    this.bus.unregister(this)
  }

  override fun play() {
    //todo api update
  }

  override fun previous() {
    //todo api update
  }

  override fun next() {
    //todo api update

  }

  override fun lfmLove(): Boolean {
    //todo api update
    return true
  }
}
