package com.kelsos.mbrc.presenters

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.MuteChangeEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.RepeatChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.events.ui.VolumeChangeEvent
import com.kelsos.mbrc.interactors.MuteInteractor
import com.kelsos.mbrc.interactors.PlayerInteractor
import com.kelsos.mbrc.interactors.PlayerStateInteractor
import com.kelsos.mbrc.interactors.RepeatInteractor
import com.kelsos.mbrc.interactors.ShuffleInteractor
import com.kelsos.mbrc.interactors.TrackCoverInteractor
import com.kelsos.mbrc.interactors.TrackInfoInteractor
import com.kelsos.mbrc.interactors.TrackPositionInteractor
import com.kelsos.mbrc.interactors.VolumeInteractor
import com.kelsos.mbrc.task
import com.kelsos.mbrc.ui.views.MainView
import com.kelsos.mbrc.utilities.ErrorHandler
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.viewmodels.MainViewModel
import roboguice.inject.ContextSingleton
import rx.Observable
import rx.Subscription
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ContextSingleton class MainViewPresenterImpl : MainViewPresenter {
  @Inject private lateinit var errorHandler: ErrorHandler
  @Inject private lateinit var model: MainViewModel
  @Inject private lateinit var playerInteractor: PlayerInteractor
  @Inject private lateinit var trackInfoInteractor: TrackInfoInteractor
  @Inject private lateinit var volumeInteractor: VolumeInteractor
  @Inject private lateinit var shuffleInteractor: ShuffleInteractor
  @Inject private lateinit var repeatInteractor: RepeatInteractor
  @Inject private lateinit var muteInteractor: MuteInteractor
  @Inject private lateinit var coverInteractor: TrackCoverInteractor
  @Inject private lateinit var positionInteractor: TrackPositionInteractor
  @Inject private lateinit var playerStateInteractor: PlayerStateInteractor

  @Inject private lateinit var bus: RxBus

  private var mainView: MainView? = null
  private var positionUpdate: Subscription? = null

  override fun bind(mainView: MainView) {
    this.mainView = mainView
  }

  override fun onPause() {
    bus.unregister(this)
  }

  override fun onResume() {
    loadTrackInfo()
    loadCover()
    loadShuffle()
    loadRepeat()
    loadVolume()
    loadPosition()
    loadPlayerState()
    model.loadComplete()
    subscribe()
  }

  private fun subscribe() {
    bus.registerOnMain(this, VolumeChangeEvent::class.java, { this.onVolumeChangedEvent(it) })
    bus.registerOnMain(this, RepeatChange::class.java, { this.onRepeatChangedEvent(it) })
    bus.registerOnMain(this, TrackInfoChangeEvent::class.java, { this.onTrackInfoChangedEvent(it) })
    bus.registerOnMain(this, CoverChangedEvent::class.java, { this.onCoverChangedEvent(it) })
    bus.registerOnMain(this, PlayStateChange::class.java, { this.onPlayStateChanged(it) })
    bus.registerOnMain(this, MuteChangeEvent::class.java, { this.onMuteChanged(it) })
  }

  private fun loadPlayerState() {
    if (model.isLoaded) {
      mainView?.updatePlayState(model.playState)
    } else {
      playerStateInteractor.state.doOnNext({ model.playState = it }).subscribe({
        mainView?.updatePlayState(it)
        updatePlaystate(it)
      }, { errorHandler.handleThrowable(it) })
    }
  }

  private fun loadTrackInfo() {
    if (model.trackInfo.isEmpty()) {
      trackInfoInteractor.execute(false).subscribe({
        model.trackInfo = it
        mainView?.updateTrackInfo(it)
      }, { errorHandler.handleThrowable(it) })
    } else {
      mainView?.updateTrackInfo(model.trackInfo)
    }
  }

  private fun loadCover() {
    if (model.trackCover == null) {
      coverInteractor.execute(false).subscribe({
        model.trackCover = it
        mainView?.updateCover(it)
      }, { errorHandler.handleThrowable(it) })
    } else {
      mainView?.updateCover(model.trackCover)
    }
  }

  private fun loadShuffle() {
    if (model.isLoaded) {
      mainView?.updateShuffle(model.shuffle)
    } else {
      shuffleInteractor.getShuffle().subscribe({
        model.shuffle = it
        mainView?.updateShuffle(it)
      }, { errorHandler.handleThrowable(it) })
    }
  }

  private fun loadRepeat() {
    if (model.isLoaded) {
      mainView?.updateRepeat(model.repeat)
    } else {
      repeatInteractor.getRepeat()
          .doOnNext({ model.repeat = it })
          .subscribe({
            mainView?.updateRepeat(it)
          }, { errorHandler.handleThrowable(it) })
    }
  }

  private fun loadVolume() {
    if (model.isLoaded) {
      mainView?.updateVolume(model.volume)
    } else {
      volumeInteractor.getVolume().doOnNext({ model.volume = it })
          .subscribe({
            mainView?.updateVolume(it!!)
          }, { errorHandler.handleThrowable(it) })
    }
  }

  private fun loadPosition() {
    if (model.isLoaded) {
      mainView?.updatePosition(model.position)
    } else {
      positionInteractor.getPosition()
          .doOnNext {
            model.position = it
          }
          .subscribe({ mainView?.updatePosition(it) }) { errorHandler.handleThrowable(it) }
    }
  }

  override fun onPlayPausePressed() {
    performAction(PlayerAction.PLAY_PLAUSE)
  }

  override fun onPreviousPressed() {
    performAction(PlayerAction.PREVIOUS)
  }

  override fun onNextPressed() {
    performAction(PlayerAction.NEXT)
  }

  private fun performAction(action: String) {
    playerInteractor.performAction(action).task().subscribe(
        {

        }
    ) { errorHandler.handleThrowable(it) }
  }

  override fun onStopPressed() {
    performAction(PlayerAction.STOP)
  }

  override fun onMutePressed() {
    muteInteractor.toggle().task().subscribe({
      model.isMuted = it!!
      mainView?.updateMute(it)
    }) { errorHandler.handleThrowable(it) }
  }

  override fun onShufflePressed() {
    shuffleInteractor.updateShuffle(Shuffle.TOGGLE)
        .subscribe({
          model.shuffle = it
          mainView!!.updateShuffle(it)
        }, { errorHandler.handleThrowable(it) })
  }

  override fun onRepeatPressed() {
    repeatInteractor.setRepeat(Repeat.CHANGE).task()
        .subscribe({ mainView?.updateRepeat(it) })
        { errorHandler.handleThrowable(it) }
  }

  override fun onVolumeChange(volume: Int) {
    volumeInteractor.setVolume(volume).task().subscribe(
        { model.volume = it },
        { errorHandler.handleThrowable(it) })
  }

  override fun onPositionChange(position: Int) {
    stopPositionUpdate()
    updatePosition(positionInteractor.setPosition(position))
  }

  fun updatePosition(positionObservable: Observable<TrackPosition>) {
    positionObservable.task().doOnNext({ model.position = it })
        .subscribe({ startPositionUpdate() }
        ) { errorHandler.handleThrowable(it) }
  }

  override fun onScrobbleToggle() {

  }

  override fun onLfmLoveToggle() {

  }

  fun onVolumeChangedEvent(event: VolumeChangeEvent) {
    model.volume = event.volume
    mainView?.updateVolume(event.volume)
  }

  fun onRepeatChangedEvent(event: RepeatChange) {
    model.repeat = event.mode
    mainView?.updateRepeat(event.mode)
  }

  fun onTrackInfoChangedEvent(event: TrackInfoChangeEvent) {
    Timber.v("Received change event")
    model.trackInfo = event.trackInfo
    mainView!!.updateTrackInfo(event.trackInfo)
    startPositionUpdate()
    updatePosition(positionInteractor.getPosition())
  }

  fun onCoverChangedEvent(event: CoverChangedEvent) {
    model.trackCover = event.cover
    mainView!!.updateCover(event.cover)
  }

  fun onPlayStateChanged(event: PlayStateChange) {
    model.playState = event.state
    mainView!!.updatePlayState(event.state)
    updatePlaystate(event.state)
  }

  private fun updatePlaystate(state: String) {
    if (PlayerState.PLAYING == state) {
      startPositionUpdate()
    } else {
      stopPositionUpdate()
    }
  }

  private fun stopPositionUpdate() {
    Timber.v("Track now is either paused or stoped")
    positionUpdate?.unsubscribe()
    positionUpdate = null
  }

  private fun startPositionUpdate() {
    if (positionUpdate != null) {
      return
    }

    Timber.v("Track is now playing")
    positionUpdate = Observable.interval(0, 1, TimeUnit.SECONDS).task().takeWhile {
      val position = model.position
      position.current < position.total
    }.subscribe({
      val modelPosition = model.position
      val newPosition = TrackPosition(modelPosition.current + 1000, modelPosition.total)
      model.position = newPosition
      mainView?.updatePosition(newPosition)
    }, { errorHandler.handleThrowable(it) })
  }

  fun onMuteChanged(event: MuteChangeEvent) {
    model.isMuted = event.isMute
    mainView?.updateMute(event.isMute)
  }
}
