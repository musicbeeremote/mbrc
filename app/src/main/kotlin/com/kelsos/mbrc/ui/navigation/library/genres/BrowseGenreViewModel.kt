package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import kotlinx.coroutines.flow.Flow

class BrowseGenreViewModel(
  repository: GenreRepository,
) : ViewModel() {
  val genres: Flow<PagingData<Genre>> = repository.getAll()
}
