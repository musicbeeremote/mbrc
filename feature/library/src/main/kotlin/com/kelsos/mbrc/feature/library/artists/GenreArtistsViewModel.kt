package com.kelsos.mbrc.feature.library.artists

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.data.library.artist.ArtistRepository
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GenreArtistsViewModel(
  private val repository: ArtistRepository,
  queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseArtistViewModel(queueHandler, librarySettings, connectionStateFlow) {
  private val genre: MutableSharedFlow<Long> = MutableSharedFlow(replay = 1)

  override val artists: Flow<PagingData<Artist>> =
    genre
      .flatMapLatest { id ->
        repository.getArtistByGenre(id)
      }.cachedIn(viewModelScope)

  fun load(id: Long) {
    viewModelScope.launch {
      genre.emit(id)
    }
  }
}
