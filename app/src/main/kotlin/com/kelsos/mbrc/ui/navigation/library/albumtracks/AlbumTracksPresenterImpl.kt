package com.kelsos.mbrc.ui.navigation.library.albumtracks

import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import timber.log.Timber
import javax.inject.Inject

class AlbumTracksPresenterImpl
@Inject
constructor(
    private val repository: TrackRepository
) : BasePresenter<AlbumTracksView>(),
    AlbumTracksPresenter {

  override fun load(album: AlbumInfo) {
    val request = if (album.album.isEmpty()) {
      repository.getNonAlbumTracks(album.artist)
    } else {
      repository.getAlbumTracks(album.album, album.artist)
    }

    addDisposable(request.subscribe({
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
