package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.constants.Constants.PAGE_SIZE
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.LibraryAlbumInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.ui.views.BrowseAlbumView
import timber.log.Timber
import javax.inject.Inject

class BrowseAlbumPresenterImpl
@Inject constructor(private val queueInteractor: QueueInteractor,
                    private val albumInteractor: LibraryAlbumInteractor): BrowseAlbumPresenter {

  private var view: BrowseAlbumView? = null

  override fun attachView(view: BrowseAlbumView) {
    this.view = view
  }

  override fun detachView() {
    this.view = null
  }

  override fun queue(album: Album, @Action action: String) {
    queueInteractor.execute(MetaDataType.ALBUM, action, album.id).subscribe({

    }, { Timber.v(it) })
  }

  override fun load(page: Int) {
    Timber.v("Loading page %d", page)

    albumInteractor.getPage(page * PAGE_SIZE)
        .task()
        .subscribe({
          view?.updateData(it)
        }, { Timber.v(it) })
  }
}
