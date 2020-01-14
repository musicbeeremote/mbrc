package com.kelsos.mbrc.features.library.artists

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow

class ArtistViewModel(repository: ArtistRepository) : BaseViewModel<LibraryResult>() {
  val artists: Flow<PagingData<Artist>> = repository.getAll().cachedIn(viewModelScope)
}
