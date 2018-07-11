package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking


class BrowseGenreViewModel(
  private val repository: GenreRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  val genres: LiveData<PagedList<GenreEntity>>
  val indexes: LiveData<List<String>>

  init {
    val genres = runBlocking(dispatchers.database) {
      repository.allGenres()
    }

    this.genres = genres.factory.paged()
    this.indexes = genres.indexes
  }

  fun reload() {
    launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}