package com.kelsos.mbrc.feature.library.tracks

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.track.PagingTrackQuery
import com.kelsos.mbrc.core.data.library.track.TrackRepository
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.albums.AlbumInfo
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumTracksViewModel(
  private val repository: TrackRepository,
  private val queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseTrackViewModel(queueHandler, librarySettings, connectionStateFlow) {
  private val albumInfo = MutableSharedFlow<AlbumInfo>(replay = 1)

  override val tracks =
    albumInfo.flatMapLatest {
      val query =
        if (it.album.isEmpty()) {
          PagingTrackQuery.NonAlbum(artist = it.artist)
        } else {
          PagingTrackQuery.Album(artist = it.artist, album = it.album)
        }
      repository.getTracks(query)
    }

  fun load(album: AlbumInfo) {
    viewModelScope.launch {
      albumInfo.emit(album)
    }
  }

  fun queueAlbum(album: AlbumInfo) {
    viewModelScope.launch {
      if (!checkConnection()) {
        emit(TrackUiMessage.NetworkUnavailable)
        return@launch
      }
      val queueResult = queueHandler.queueAlbum(
        type = Queue.Now,
        album = album.album,
        artist = album.artist
      )
      if (queueResult.isSuccess) {
        emit(TrackUiMessage.QueueSuccess(queueResult.getOrNull() ?: 0))
      } else {
        emit(TrackUiMessage.QueueFailed)
      }
    }
  }
}
