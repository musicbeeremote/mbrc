package com.kelsos.mbrc.ui.activities.profile.artist

import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.AlbumRepository
import timber.log.Timber
import javax.inject.Inject

class ArtistAlbumsPresenterImpl
@Inject constructor(private val repository: AlbumRepository) :
    BasePresenter<ArtistAlbumsView>(),
    ArtistAlbumsPresenter {
  override fun load(artist: String) {
    addSubcription(repository.getAlbumsByArtist(artist).subscribe ({
      view?.update(it)
    }) {
      Timber.v(it)
    })
  }
}
