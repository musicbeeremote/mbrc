package com.kelsos.mbrc.feature.library.genres

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow

abstract class BaseGenreViewModel(
  private val queueHandler: QueueHandler,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<GenreUiMessage>(connectionStateFlow) {
  abstract val genres: Flow<PagingData<Genre>>

  fun queue(queue: Queue, genre: Genre) {
    if (queue == Queue.Default) {
      launchDefault(GenreUiMessage.OpenArtists(genre))
      return
    }

    launchQueue { queueHandler.queueGenre(queue, genre.genre) }
  }

  fun goToAlbums(genre: Genre) {
    launchDefault(GenreUiMessage.OpenAlbums(genre))
  }
}
