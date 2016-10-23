package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractor
import com.kelsos.mbrc.ui.views.ArtistAlbumsView
import timber.log.Timber
import javax.inject.Inject

class ArtistAlbumPresenterImpl
@Inject constructor(private var artistAlbumInteractor: ArtistAlbumInteractor,
                    private var queueInteractor: QueueInteractor) : ArtistAlbumPresenter {

  private var view: ArtistAlbumsView? = null

  override fun load(artistId: Long) {
    artistAlbumInteractor.getArtistAlbums(artistId)
        .subscribe({ view?.update(it) }) { view?.showLoadFailed() }
  }

  override fun bind(view: ArtistAlbumsView) {

    this.view = view
  }

  override fun queue(@Action action: String, album: Album) {
    queueInteractor.execute(MetaDataType.ALBUM, action, album.id).subscribe({
      if (it!!) {
        view?.queueSuccess()
      }

    }) { Timber.v(it, "failed to queue the album") }
  }
}
