package com.kelsos.mbrc.presenters

import com.google.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.constants.Constants.PAGE_SIZE
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.interactors.LibraryAlbumInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.ui.views.BrowseAlbumView
import roboguice.util.Ln

class BrowseAlbumPresenterImpl : BrowseAlbumPresenter {

  @Inject private lateinit var queueInteractor: QueueInteractor
  @Inject private lateinit var albumInteractor: LibraryAlbumInteractor
  private var view: BrowseAlbumView? = null

  override fun bind(view: BrowseAlbumView) {
    this.view = view
  }

  override fun queue(album: Album, @Queue.Action action: String) {
    queueInteractor.execute(MetaDataType.ALBUM, action, album.id).subscribe({

    }, { Ln.v(it) })
  }

  override fun load() {
    albumInteractor.execute(0, PAGE_SIZE).task().subscribe({
      view?.clearData()
      view?.updateData(it)
    }, { Ln.v(it) })
  }

  override fun load(page: Int) {
    albumInteractor.execute(page * PAGE_SIZE, PAGE_SIZE).task().subscribe({
      view?.updateData(it)
    }, { Ln.v(it) })
  }
}
