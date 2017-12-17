package com.kelsos.mbrc.ui.navigation.library.artistalbums

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ArtistAlbumsPresenterImpl
@Inject
constructor(
  private val repository: AlbumRepository,
  private val queue: QueueHandler
) : BasePresenter<ArtistAlbumsView>(), ArtistAlbumsPresenter {

  override fun load(artist: String) {
    scope.launch {
      try {
        val data = repository.getAlbumsByArtist(artist)
        val liveData = data.paged()
        liveData.observe(
          this@ArtistAlbumsPresenterImpl,
          {
            if (it != null) {
              view().update(it)
            }
          }
        )
      } catch (e: Exception) {
        Timber.v(e)
      }
    }
  }

  override fun queue(action: String, album: Album) {
    scope.launch {
      val artist = album.artist
      val albumName = album.album
      val (success, tracks) = queue.queueAlbum(action, albumName, artist)
      view().queue(success, tracks)
    }
  }
}
