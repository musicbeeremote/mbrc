package com.kelsos.mbrc.ui.navigation.library.artist_albums

import com.kelsos.mbrc.library.albums.AlbumRepository
import com.kelsos.mbrc.mvp.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class ArtistAlbumsPresenterImpl
@Inject constructor(private val repository: AlbumRepository) :
    BasePresenter<ArtistAlbumsView>(),
    ArtistAlbumsPresenter {
  override fun load(artist: String) {
    addDisposable(repository.getAlbumsByArtist(artist).subscribe ({
      view?.update(it)
    }) {
      Timber.v(it)
    })
  }
}
