package com.kelsos.mbrc.feature.library.albums

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.AlbumViewMode
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GenreAlbumsViewModel(
  private val repository: AlbumRepository,
  queueHandler: QueueHandler,
  private val librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseAlbumViewModel(queueHandler, librarySettings, connectionStateFlow) {
  private val genreFilter: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1)

  val sortPreference: Flow<AlbumSortPreference> = librarySettings.albumSortPreferenceFlow
  val albumViewMode: Flow<AlbumViewMode> = librarySettings.albumViewModeFlow

  override val albums: Flow<PagingData<Album>> =
    combine(genreFilter, sortPreference) { genreId, sort ->
      Triple(genreId, sort.field, sort.order)
    }.flatMapLatest { (genreId, field, order) ->
      repository.getAlbumsByGenre(genreId, field, order)
    }.cachedIn(viewModelScope)

  fun load(genreId: Long) {
    viewModelScope.launch {
      genreFilter.emit(genreId)
    }
  }

  fun updateSortPreference(preference: AlbumSortPreference) {
    viewModelScope.launch {
      librarySettings.setAlbumSortPreference(preference)
    }
  }

  fun toggleViewMode() {
    viewModelScope.launch {
      val current = librarySettings.albumViewModeFlow.first()
      val next = when (current) {
        AlbumViewMode.AUTO -> AlbumViewMode.LIST
        AlbumViewMode.LIST -> AlbumViewMode.GRID
        AlbumViewMode.GRID -> AlbumViewMode.LIST
      }
      librarySettings.setAlbumViewMode(next)
    }
  }
}
