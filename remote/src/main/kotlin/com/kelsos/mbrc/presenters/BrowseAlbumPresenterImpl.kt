package com.kelsos.mbrc.presenters

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.constants.Constants.PAGE_SIZE
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.interactors.LibraryAlbumInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.ui.views.BrowseAlbumView
import roboguice.util.Ln
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BrowseAlbumPresenterImpl : BrowseAlbumPresenter {

  @Inject private lateinit var queueInteractor: QueueInteractor
  @Inject private lateinit var albumInteractor: LibraryAlbumInteractor
  private var view: BrowseAlbumView? = null

  override fun bind(view: BrowseAlbumView) {
    this.view = view
  }

  override fun queue(album: Album, @Queue.Action action: String) {
    queueInteractor.execute(MetaDataType.ALBUM, action, album.id.toInt()).subscribe({

    }, { Ln.v(it) })
  }

  override fun load() {
    albumInteractor.execute(0,
        PAGE_SIZE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.clearData()
      view?.updateData(it)
    }, { Ln.v(it) })
  }

  override fun load(page: Int) {
    albumInteractor.execute(page * PAGE_SIZE,
        PAGE_SIZE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.updateData(it)
    }, { Ln.v(it) })
  }
}
