package com.kelsos.mbrc.ui.activities.profile.album

import com.kelsos.mbrc.domain.AlbumInfo
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.TrackRepository
import timber.log.Timber
import javax.inject.Inject

class AlbumTracksPresenterImpl
@Inject constructor(private val repository: TrackRepository) :
    BasePresenter<AlbumTracksView>(),
    AlbumTracksPresenter {
  override fun load(album: AlbumInfo) {
    val request = if (album.album.isNullOrEmpty()) {
      repository.getNonAlbumTracks(album.artist)
    } else {
      repository.getAlbumTracks(album.album)
    }

    addSubcription(request.subscribe ({
      view?.update(it)
    }) {
      Timber.v(it)
    })
  }
}
