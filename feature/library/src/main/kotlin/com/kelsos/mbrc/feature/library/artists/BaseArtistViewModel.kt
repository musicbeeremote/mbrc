package com.kelsos.mbrc.feature.library.artists

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseArtistViewModel(
  private val queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<ArtistUiMessage>(librarySettings, connectionStateFlow) {
  abstract val artists: Flow<PagingData<Artist>>

  fun queue(queue: Queue, artist: Artist) {
    if (queue == Queue.Default) {
      launchDefault(ArtistUiMessage.OpenArtistAlbums(artist))
      return
    }

    viewModelScope.launch {
      if (!checkConnection()) {
        emit(ArtistUiMessage.NetworkUnavailable)
        return@launch
      }

      val result = queueHandler.queueArtist(queue, artist.artist)

      val message =
        if (result.isSuccess) {
          ArtistUiMessage.QueueSuccess(result.getOrNull() ?: 0)
        } else {
          ArtistUiMessage.QueueFailed
        }

      emit(message)
    }
  }
}
