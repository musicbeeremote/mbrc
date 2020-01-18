package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow

class AlbumViewModel(repository: AlbumRepository) : ViewModel() {
  val albums: Flow<PagingData<Album>> = repository.getAll().cachedIn(viewModelScope)
}
