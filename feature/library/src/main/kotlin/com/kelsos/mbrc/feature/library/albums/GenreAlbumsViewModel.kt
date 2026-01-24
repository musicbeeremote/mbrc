package com.kelsos.mbrc.feature.library.albums

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GenreAlbumsViewModel(
  private val repository: AlbumRepository,
  queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseAlbumViewModel(queueHandler, librarySettings, connectionStateFlow) {
  private val genreFilter: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1)

  override val albums: Flow<PagingData<Album>> =
    genreFilter
      .flatMapLatest { repository.getAlbumsByGenre(it) }
      .cachedIn(viewModelScope)

  fun load(genreId: Long) {
    viewModelScope.launch {
      genreFilter.emit(genreId)
    }
  }
}
