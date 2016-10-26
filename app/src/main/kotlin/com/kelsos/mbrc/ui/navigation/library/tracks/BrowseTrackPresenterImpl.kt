package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.LibraryTrackInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackView
import timber.log.Timber
import javax.inject.Inject

class BrowseTrackPresenterImpl
@Inject constructor(private val queueInteractor: QueueInteractor,
                    private val trackInteractor: LibraryTrackInteractor) : BrowseTrackPresenter {
  private var view: BrowseTrackView? = null

  override fun bind(view: BrowseTrackView) {
    this.view = view
  }

  override fun load() {
    trackInteractor.execute(0, 5).task().subscribe({
      view?.clearData()
      view?.appendPage(it)
    }, { Timber.v(it) })
  }

  override fun queue(track: Track, @Action action: String) {
    queueInteractor.execute(MetaDataType.TRACK, action, track.id)
        .task()
        .subscribe({ }, { Timber.v(it) })
  }

  override fun load(page: Int, totalItemsCount: Int) {
    trackInteractor.execute(page, totalItemsCount)
        .task()
        .subscribe({ view?.appendPage(it) }, { Timber.v(it) })
  }
}
