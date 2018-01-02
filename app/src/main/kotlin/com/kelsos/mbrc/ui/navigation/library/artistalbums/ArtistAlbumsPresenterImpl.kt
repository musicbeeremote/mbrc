package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.mvp.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class ArtistAlbumsPresenterImpl
@Inject
constructor(
    private val repository: AlbumRepository
) : BasePresenter<ArtistAlbumsView>(),
    ArtistAlbumsPresenter {

  private lateinit var albums: LiveData<List<AlbumEntity>>

  override fun load(artist: String) {
    addDisposable(repository.getAlbumsByArtist(artist).subscribe({
      albums = it
      albums.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
    }) {
      Timber.v(it)
    })
  }
}
