package com.kelsos.mbrc.feature.library.albums

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseAlbumViewModel(
  private val queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<AlbumUiMessage>(librarySettings, connectionStateFlow) {
  abstract val albums: Flow<PagingData<Album>>

  fun queue(queue: Queue, album: Album) {
    if (queue == Queue.Default) {
      launchDefault(AlbumUiMessage.OpenAlbumTracks(album))
      return
    }

    viewModelScope.launch {
      if (!checkConnection()) {
        emit(AlbumUiMessage.NetworkUnavailable)
        return@launch
      }

      val result = queueHandler.queueAlbum(queue, album.album, album.artist)

      val message =
        if (result.isSuccess) {
          AlbumUiMessage.QueueSuccess(result.getOrNull() ?: 0)
        } else {
          AlbumUiMessage.QueueFailed
        }

      emit(message)
    }
  }
}
