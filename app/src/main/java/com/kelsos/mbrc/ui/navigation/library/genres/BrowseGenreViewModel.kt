package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BrowseGenreViewModel(
  private val repository: GenreRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  val genres: Flow<PagingData<Genre>> = repository.getAll().cachedIn(viewModelScope)

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}
