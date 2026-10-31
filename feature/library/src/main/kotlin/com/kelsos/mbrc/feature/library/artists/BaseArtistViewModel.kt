package com.kelsos.mbrc.feature.library.artists

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow

abstract class BaseArtistViewModel(
  private val queueHandler: QueueHandler,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<ArtistUiMessage>(connectionStateFlow) {
  abstract val artists: Flow<PagingData<Artist>>

  fun queue(queue: Queue, artist: Artist) {
    if (queue == Queue.Default) {
      launchDefault(ArtistUiMessage.OpenArtistAlbums(artist))
      return
    }

    launchQueue { queueHandler.queueArtist(queue, artist.artist) }
  }
}
