package com.kelsos.mbrc.ui.navigation.library.artistalbums

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber


class ArtistAlbumsPresenterImpl

constructor(
  private val repository: AlbumRepository
) : BasePresenter<ArtistAlbumsView>(),
  ArtistAlbumsPresenter {

  private lateinit var albums: LiveData<PagedList<AlbumEntity>>

  override fun load(artist: String) {
    disposables += repository.getAlbumsByArtist(artist).subscribe({

      albums = it.paged()

      albums.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
    }) {
      Timber.v(it)
    }
  }
}