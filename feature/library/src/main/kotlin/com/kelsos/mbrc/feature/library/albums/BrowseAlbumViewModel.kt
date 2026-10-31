package com.kelsos.mbrc.feature.library.albums

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import com.kelsos.mbrc.feature.library.LibrarySearchModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

private data class AlbumSearchParams(
  val keyword: String,
  val field: AlbumSortField,
  val order: SortOrder
)

@OptIn(ExperimentalCoroutinesApi::class)
class BrowseAlbumViewModel(
  private val repository: AlbumRepository,
  queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow,
  private val searchModel: LibrarySearchModel
) : BaseAlbumViewModel(queueHandler, librarySettings, connectionStateFlow) {
  override val albums: Flow<PagingData<Album>> =
    combine(searchModel.term, sortPreference) { keyword, sort ->
      AlbumSearchParams(keyword, sort.field, sort.order)
    }.flatMapLatest { (keyword, field, order) ->
      if (keyword.isEmpty()) {
        repository.getAll(field, order)
      } else {
        repository.search(keyword, field, order)
      }
    }.cachedIn(viewModelScope)

  val showSync = searchModel.term.map { it.isEmpty() }
}
