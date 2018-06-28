package com.kelsos.mbrc.ui.navigation.library.albumtracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber


class AlbumTracksPresenterImpl

constructor(
  private val repository: TrackRepository
) : BasePresenter<AlbumTracksView>(),
  AlbumTracksPresenter {

  private lateinit var tracks: LiveData<PagedList<TrackEntity>>

  override fun load(album: AlbumInfo) {
    val request = if (album.album.isEmpty()) {
      repository.getNonAlbumTracks(album.artist)
    } else {
      repository.getAlbumTracks(album.album, album.artist)
    }

    disposables += request.subscribe({
      tracks = it.paged()
      tracks.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
    }) {
      Timber.v(it)
    }
  }
}