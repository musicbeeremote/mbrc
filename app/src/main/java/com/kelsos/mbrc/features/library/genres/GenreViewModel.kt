package com.kelsos.mbrc.features.library.genres

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged

class GenreViewModel(
  repository: GenreRepository,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryResult>(dispatchers) {
  val genres: LiveData<PagedList<Genre>>
  val indexes: LiveData<List<String>>

  init {
    val genres = repository.allGenres()
    this.genres = genres.factory.paged()
    this.indexes = genres.indexes
  }
}