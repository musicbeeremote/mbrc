package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.interactors.LibraryTrackInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.ui.views.BrowseTrackView
import roboguice.util.Ln

class BrowseTrackPresenterImpl : BrowseTrackPresenter {
  private var view: BrowseTrackView? = null
  @Inject private lateinit var queueInteractor: QueueInteractor
  @Inject private lateinit var trackInteractor: LibraryTrackInteractor

  override fun bind(view: BrowseTrackView) {
    this.view = view
  }

  override fun load() {
    trackInteractor.execute(0, 5).task().subscribe({
      view?.clearData()
      view?.appendPage(it)
    }, { Ln.v(it) })
  }

  override fun queue(track: Track, @Queue.Action action: String) {
    queueInteractor.execute(MetaDataType.TRACK, action, track.id)
        .task()
        .subscribe({ }, { Ln.v(it) })
  }

  override fun load(page: Int, totalItemsCount: Int) {
    trackInteractor.execute(page, totalItemsCount)
        .task()
        .subscribe({ view?.appendPage(it) }, { Ln.v(it) })
  }
}
