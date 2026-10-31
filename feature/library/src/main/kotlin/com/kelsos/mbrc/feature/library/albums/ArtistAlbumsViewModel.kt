package com.kelsos.mbrc.feature.library.albums

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow

class ArtistAlbumsViewModel(
  private val repository: AlbumRepository,
  queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : FilteredAlbumViewModel<String>(queueHandler, librarySettings, connectionStateFlow) {
  override fun albumsFor(key: String, sort: AlbumSortPreference): Flow<PagingData<Album>> =
    repository.getAlbumsByArtist(key, sort.field, sort.order)
}
