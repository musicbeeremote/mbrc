package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import kotlinx.coroutines.flow.Flow

class GenreViewModel(repository: GenreRepository) : BaseViewModel<LibraryResult>() {
  val genres: Flow<PagingData<Genre>> = repository.getAll().cachedIn(viewModelScope)
}
