package com.kelsos.mbrc.feature.library.albums

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow

class GenreAlbumsViewModel(
  private val repository: AlbumRepository,
  queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : FilteredAlbumViewModel<Long>(queueHandler, librarySettings, connectionStateFlow) {
  override fun albumsFor(key: Long, sort: AlbumSortPreference): Flow<PagingData<Album>> =
    repository.getAlbumsByGenre(key, sort.field, sort.order)
}
