package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.paging.PagingData
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.PagingTrackQuery
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapMerge

private data class AlbumPayload(val album: String, val artist: String)

class AlbumTrackViewModel(
  private val repository: TrackRepository,
  private val workHandler: WorkHandler
) : BaseViewModel<UiMessageBase>() {
  private val albumFlow: MutableSharedFlow<AlbumPayload> = MutableSharedFlow()

  @OptIn(FlowPreview::class)
  val tracks: Flow<PagingData<Track>> = albumFlow.flatMapMerge { (album, artist) ->
    repository.getTracks(query = PagingTrackQuery.Album(album = album, artist = artist))
  }

  fun load(album: String, artist: String) {
    albumFlow.tryEmit(AlbumPayload(album = album, artist = artist))
  }

  fun queue(action: Queue, item: Track) {
    workHandler.queue(item.id, Meta.Track, action)
  }
}
