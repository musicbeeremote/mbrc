package com.kelsos.mbrc.ui.mini_control

import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.annotations.PlayerAction.Action
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.PlayerInteractor
import com.kelsos.mbrc.interactors.PlayerStateInteractor
import com.kelsos.mbrc.interactors.TrackCoverInteractor
import com.kelsos.mbrc.interactors.TrackInfoInteractor
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.ErrorHandler
import com.kelsos.mbrc.utilities.RxBus
import toothpick.smoothie.annotations.ContextSingleton
import javax.inject.Inject

@ContextSingleton
class MiniControlPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val interactor: PlayerInteractor,
                    private val handler: ErrorHandler,
                    private val model: MiniControlModel,
                    private val coverInteractor: TrackCoverInteractor,
                    private val infoInteractor: TrackInfoInteractor,
                    private val playerStateInteractor: PlayerStateInteractor) :
    MiniControlPresenter,
    BasePresenter<MiniControlView>() {

  override fun onNextPressed() {
    action(PlayerAction.NEXT)
  }

  private fun action(@Action action: String) {
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

  override fun attach(view: MiniControlView) {
    super.attach(view)
    bus.registerOnMain(this, CoverChangedEvent::class.java, { this.onCoverAvailable(it) })
    bus.registerOnMain(this, PlayStateChange::class.java, { this.onPlayStateChange(it) })
    bus.registerOnMain(this, TrackInfoChangeEvent::class.java, { this.onTrackInfoChange(it) })
  }

  override fun detach() {
    super.detach()
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
        .doOnNext({ model.setPlayerState(it) })
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
