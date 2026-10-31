package com.kelsos.mbrc.feature.library.albums

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

/**
 * The albums of one artist or genre, picked by the [K] key a drilldown screen passes to [load].
 * Nothing is fetched until the first [load].
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class FilteredAlbumViewModel<K : Any>(
  queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseAlbumViewModel(queueHandler, librarySettings, connectionStateFlow) {
  private val filter = MutableStateFlow<K?>(null)

  override val albums: Flow<PagingData<Album>> =
    combine(filter.filterNotNull(), sortPreference, ::Pair)
      .flatMapLatest { (key, sort) -> albumsFor(key, sort) }
      .cachedIn(viewModelScope)

  fun load(key: K) {
    filter.value = key
  }

  protected abstract fun albumsFor(key: K, sort: AlbumSortPreference): Flow<PagingData<Album>>
}
