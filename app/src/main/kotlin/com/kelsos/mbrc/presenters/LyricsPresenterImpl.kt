package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.events.ui.LyricsChangedEvent
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.TrackLyricsInteractor
import com.kelsos.mbrc.ui.views.LyricsView
import com.kelsos.mbrc.utilities.RxBus
import timber.log.Timber
import javax.inject.Inject

class LyricsPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val lyricsInteractor: TrackLyricsInteractor) : LyricsPresenter {

  private var view: LyricsView? = null

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
        { Timber.v(it) })
  }
}
