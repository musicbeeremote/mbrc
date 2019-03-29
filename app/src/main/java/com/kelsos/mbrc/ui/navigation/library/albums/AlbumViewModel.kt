package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import kotlinx.coroutines.flow.Flow

class AlbumViewModel(repository: AlbumRepository) : BaseViewModel<LibraryResult>() {
  val albums: Flow<PagingData<Album>> = repository.getAll()
}
