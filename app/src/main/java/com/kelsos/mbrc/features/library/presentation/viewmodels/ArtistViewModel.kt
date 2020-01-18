package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import kotlinx.coroutines.flow.Flow

class ArtistViewModel(repository: ArtistRepository) : ViewModel() {
  val artists: Flow<PagingData<Artist>> = repository.getAll().cachedIn(viewModelScope)
}
