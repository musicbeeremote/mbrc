package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch

class GenreViewModel(
  private val repository: GenreRepository,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryResult>(dispatchers) {
  val genres: LiveData<PagedList<Genre>>
  val indexes: LiveData<List<String>>

  init {
    val genres = repository.allGenres()
    this.genres = genres.factory.paged()
    this.indexes = genres.indexes
  }

  fun reload() {
    scope.launch {
      repository.getRemote()
    }
  }
}