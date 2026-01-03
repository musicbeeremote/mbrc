package com.kelsos.mbrc.feature.library.genres

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseGenreViewModel(
  private val queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<GenreUiMessage>(librarySettings, connectionStateFlow) {
  abstract val genres: Flow<PagingData<Genre>>

  fun queue(queue: Queue, genre: Genre) {
    if (queue == Queue.Default) {
      launchDefault(GenreUiMessage.OpenArtists(genre))
      return
    }

    viewModelScope.launch {
      if (!checkConnection()) {
        emit(GenreUiMessage.NetworkUnavailable)
        return@launch
      }

      val result = queueHandler.queueGenre(queue, genre.genre)

      val message =
        if (result.isSuccess) {
          GenreUiMessage.QueueSuccess(result.getOrNull() ?: 0)
        } else {
          GenreUiMessage.QueueFailed
        }

      emit(message)
    }
  }
}
