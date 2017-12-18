package com.kelsos.mbrc.ui.navigation.library.albumtracks

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AlbumTracksPresenterImpl
@Inject
constructor(
  private val repository: TrackRepository,
  private val queue: QueueHandler
) : BasePresenter<AlbumTracksView>(), AlbumTracksPresenter {

  private lateinit var tracks: LiveData<PagedList<Track>>

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

        tracks = data.paged()
        tracks.observe(
          this@AlbumTracksPresenterImpl,
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
      val (success, tracks) = queue.queueAlbum(Queue.NOW, album, artist)
      view().queue(success, tracks)
    }
  }
}
