package com.kelsos.mbrc.ui.navigation.library.albumtracks

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AlbumTracksPresenterImpl
@Inject
constructor(
  private val repository: TrackRepository,
  private val queue: QueueHandler
) : BasePresenter<AlbumTracksView>(), AlbumTracksPresenter {

  private lateinit var tracks: Flow<PagingData<Track>>

  override fun load(album: AlbumInfo) {
    scope.launch {
      try {
        val data = when {
          album.album.isEmpty() -> {
            repository.getNonAlbumTracks(album.artist)
          }
          else -> {
            repository.getAlbumTracks(album.album, album.artist)
          }
        }

        tracks = data.cachedIn(scope)
        data.collectLatest { view().update(it) }
      } catch (e: Exception) {
        Timber.v(e)
      }
    }
  }

  override fun queue(entry: Track, action: String?) {
    scope.launch {
      val (success, tracks) = if (action == null) {
        queue.queueTrack(entry, true)
      } else {
        queue.queueTrack(entry, action, true)
      }
      view().queue(success, tracks)
    }
  }

  override fun queueAlbum(artist: String, album: String) {
    scope.launch {
      val (success, tracks) = queue.queueAlbum(LibraryPopup.NOW, album, artist)
      view().queue(success, tracks)
    }
  }
}
