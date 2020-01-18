package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import kotlinx.coroutines.flow.Flow

class GenreViewModel(repository: GenreRepository) : ViewModel() {
  val genres: Flow<PagingData<Genre>> = repository.getAll().cachedIn(viewModelScope)
}
