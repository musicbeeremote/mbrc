package com.kelsos.mbrc.features.library.details

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapMerge

class ArtistAlbumViewModel(
  private val repository: AlbumRepository,
  private val workHandler: WorkHandler
) : BaseViewModel<UiMessageBase>() {
  private val artistFlow: MutableSharedFlow<String> = MutableSharedFlow()

  @OptIn(FlowPreview::class)
  val albums: Flow<PagingData<Album>> = artistFlow.flatMapMerge {
    repository.getAlbumsByArtist(it)
  }.cachedIn(viewModelScope)

  fun load(artist: String) {
    artistFlow.tryEmit(artist)
  }

  fun queue(action: Queue, item: Album) {
    workHandler.queue(item.id, Meta.Album, action)
  }
}
