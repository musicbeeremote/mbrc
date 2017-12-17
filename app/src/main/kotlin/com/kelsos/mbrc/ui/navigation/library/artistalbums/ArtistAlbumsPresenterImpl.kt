package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import timber.log.Timber
import javax.inject.Inject

class ArtistAlbumsPresenterImpl
@Inject
constructor(
    private val repository: AlbumRepository
) : BasePresenter<ArtistAlbumsView>(),
    ArtistAlbumsPresenter {

  override fun load(artist: String) {
    addDisposable(repository.getAlbumsByArtist(artist).subscribe({
      val liveData = it.paged()
      liveData.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
    }) {
      Timber.v(it)
    })
  }
}
