package com.kelsos.mbrc.features.library.genres

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.common.state.ConnectionStateFlow
import com.kelsos.mbrc.features.library.BaseLibraryViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.queue.QueueHandler
import com.kelsos.mbrc.features.settings.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseGenreViewModel(
  private val queueHandler: QueueHandler,
  settingsManager: SettingsManager,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<GenreUiMessage>(settingsManager, connectionStateFlow) {
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
        if (result.success) {
          GenreUiMessage.QueueSuccess(result.tracks)
        } else {
          GenreUiMessage.QueueFailed
        }

      emit(message)
    }
  }
}
