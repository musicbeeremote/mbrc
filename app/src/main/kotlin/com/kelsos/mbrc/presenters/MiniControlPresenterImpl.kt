package com.kelsos.mbrc.presenters

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.PlayerInteractor
import com.kelsos.mbrc.interactors.PlayerStateInteractor
import com.kelsos.mbrc.interactors.TrackCoverInteractor
import com.kelsos.mbrc.interactors.TrackInfoInteractor
import com.kelsos.mbrc.ui.views.MiniControlView
import com.kelsos.mbrc.utilities.ErrorHandler
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.viewmodels.MiniControlModel
import roboguice.inject.ContextSingleton

@ContextSingleton class MiniControlPresenterImpl : MiniControlPresenter {
  private var view: MiniControlView? = null
  @Inject private lateinit var bus: RxBus
  @Inject private lateinit var interactor: PlayerInteractor
  @Inject private lateinit var handler: ErrorHandler
  @Inject private lateinit var model: MiniControlModel
  @Inject private lateinit var coverInteractor: TrackCoverInteractor
  @Inject private lateinit var infoInteractor: TrackInfoInteractor
  @Inject private lateinit var playerStateInteractor: PlayerStateInteractor

  override fun onNextPressed() {
    action(PlayerAction.NEXT)
  }

  private fun action(@PlayerAction.Action action: String) {
    interactor.performAction(action)
        .task()
        .subscribe({ }, { handler.handleThrowable(it) })
  }

  override fun onPreviousPressed() {
    action(PlayerAction.PREVIOUS)
  }

  override fun onPlayPause() {
    action(PlayerAction.PLAY_PLAUSE)
  }

  override fun bind(view: MiniControlView) {
    this.view = view
  }

  override fun onResume() {
    bus.registerOnMain(this, CoverChangedEvent::class.java, { this.onCoverAvailable(it) })
    bus.registerOnMain(this, PlayStateChange::class.java, { this.onPlayStateChange(it) })
    bus.registerOnMain(this, TrackInfoChangeEvent::class.java, { this.onTrackInfoChange(it) })
  }

  override fun onPause() {
    bus.unregister(this)
  }

  override fun load() {
    coverInteractor.load(false).task().subscribe({
      view?.updateCover(it)
      model.setCover(it)
    }, { handler.handleThrowable(it) })

    infoInteractor.load().task().subscribe({
      view?.updateTrack(it.artist, it.title)
      model.setArtist(it.artist)
      model.setTitle(it.title)
    }, { handler.handleThrowable(it) })

    playerStateInteractor.getState().task()
        .doOnNext( { model.setPlayerState(it) })
        .subscribe({ view?.updatePlayerState(it) },
        { handler.handleThrowable(it) })
  }

  fun onCoverAvailable(event: CoverChangedEvent) {
    view?.updateCover(event.cover)
  }

  fun onPlayStateChange(event: PlayStateChange) {
    view?.updatePlayerState(event.state)
  }

  fun onTrackInfoChange(event: TrackInfoChangeEvent) {
    val info = event.trackInfo
    view?.updateTrack(info.artist, info.title)
  }
}
