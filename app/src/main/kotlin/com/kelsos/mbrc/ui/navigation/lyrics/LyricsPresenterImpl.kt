package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.events.ui.LyricsChangedEvent
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.TrackLyricsInteractor
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.RxBus
import timber.log.Timber
import javax.inject.Inject

class LyricsPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val lyricsInteractor: TrackLyricsInteractor) :
    LyricsPresenter,
    BasePresenter<LyricsView>() {


  override fun attach(view: LyricsView) {
    super.attach(view)
    bus.register(this, LyricsChangedEvent::class.java, { this.updateLyricsData(it) })
  }

  fun updateLyricsData(update: LyricsChangedEvent) {
    view?.updateLyrics(update.lyrics)
  }

  fun load() {
    addSubcription(lyricsInteractor.execute(false)
        .task()
        .subscribe({ view?.updateLyrics(it) },
        { Timber.v(it) }))
  }
}
