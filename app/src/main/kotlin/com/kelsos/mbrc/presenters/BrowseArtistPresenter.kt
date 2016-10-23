package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.constants.Constants
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.LibraryArtistInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.ui.views.BrowseArtistView
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenter
@Inject constructor(private val artistInteractor: LibraryArtistInteractor,
                    private val queueInteractor: QueueInteractor) {
  private var view: BrowseArtistView? = null

  fun bind(view: BrowseArtistView) {
    this.view = view
  }

  fun load() {
    artistInteractor.execute().task().subscribe({
      view!!.clear()
      view!!.load(it)
    }, { Timber.v(it) })
  }

  fun queue(artist: Artist, @Action action: String) {
    queueInteractor.execute(MetaDataType.ARTIST,
        action,
        artist.id).observeOn(AndroidSchedulers.mainThread()).subscribe({
      if (it!!) {
        view!!.showEnqueueSuccess()
      } else {
        view!!.showEnqueueFailure()
      }
    }) { view!!.showEnqueueFailure() }
  }

  fun load(page: Int) {
    artistInteractor.execute(page * Constants.PAGE_SIZE).task()
        .subscribe({ view?.load(it) }, { Timber.v(it, "Failed to load") })
  }
}
