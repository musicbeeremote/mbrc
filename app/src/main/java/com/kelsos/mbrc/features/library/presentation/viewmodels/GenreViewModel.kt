package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.repositories.GenreRepository

class GenreViewModel(repository: GenreRepository) : ViewModel() {
  val genres: LiveData<PagedList<Genre>>
  val indexes: LiveData<List<String>>

  init {
    val genres = repository.allGenres()
    this.genres = genres.factory.paged()
    this.indexes = genres.indexes
  }
}