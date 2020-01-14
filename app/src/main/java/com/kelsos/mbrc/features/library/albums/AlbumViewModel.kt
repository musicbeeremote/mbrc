package com.kelsos.mbrc.features.library.albums

import androidx.paging.PagingData
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow

class AlbumViewModel(repository: AlbumRepository) : BaseViewModel<LibraryResult>() {
  val albums: Flow<PagingData<Album>> = repository.getAll()
}
