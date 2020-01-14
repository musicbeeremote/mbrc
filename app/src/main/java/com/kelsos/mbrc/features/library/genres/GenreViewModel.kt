package com.kelsos.mbrc.features.library.genres

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow

class GenreViewModel(repository: GenreRepository) : BaseViewModel<LibraryResult>() {
  val genres: Flow<PagingData<Genre>> = repository.getAll().cachedIn(viewModelScope)
}
