package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractor
import com.kelsos.mbrc.ui.views.ArtistAlbumsView
import timber.log.Timber

class ArtistAlbumPresenterImpl : ArtistAlbumPresenter {

  private var view: ArtistAlbumsView? = null

  @Inject private lateinit var artistAlbumInteractor: ArtistAlbumInteractor
  @Inject private lateinit var queueInteractor: QueueInteractor

  override fun load(artistId: Long) {
    artistAlbumInteractor.getArtistAlbums(artistId)
        .subscribe({ view?.update(it) }) { view?.showLoadFailed() }
  }

  override fun bind(view: ArtistAlbumsView) {

    this.view = view
  }

  override fun queue(@Queue.Action action: String, album: Album) {
    queueInteractor.execute(MetaDataType.ALBUM, action, album.id).subscribe({
      if (it!!) {
        view?.queueSuccess()
      }

    }) { t -> Timber.v(t, "failed to queue the album") }
  }
}
