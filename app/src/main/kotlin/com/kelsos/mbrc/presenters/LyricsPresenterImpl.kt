package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.events.ui.LyricsChangedEvent
import com.kelsos.mbrc.interactors.TrackLyricsInteractor
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.ui.views.LyricsView
import com.kelsos.mbrc.utilities.RxBus
import roboguice.util.Ln

class LyricsPresenterImpl : LyricsPresenter {

  private var view: LyricsView? = null
  @Inject private lateinit var bus: RxBus
  @Inject private lateinit var lyricsInteractor: TrackLyricsInteractor

  override fun bind(view: LyricsView) {
    this.view = view
    bus.register(this, LyricsChangedEvent::class.java, { this.updateLyricsData(it) })
  }

  override fun onPause() {

  }

  override fun onResume() {
    loadLyrics()
  }

  fun updateLyricsData(update: LyricsChangedEvent) {
    view?.updateLyrics(update.lyrics)
  }

  private fun loadLyrics() {
    lyricsInteractor.execute(false).task().subscribe(
        { view?.updateLyrics(it) },
        { Ln.v(it) })
  }
}
