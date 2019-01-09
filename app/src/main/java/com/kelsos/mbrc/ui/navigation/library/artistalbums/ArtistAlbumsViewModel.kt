package com.kelsos.mbrc.ui.navigation.library.artistalbums

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import kotlinx.coroutines.flow.Flow

class ArtistAlbumsViewModel(
  repository: AlbumRepository
) : ViewModel() {
  val albums: Flow<PagingData<Album>> = repository.getAll()
}
