package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GenreViewModel(
  private val repository: GenreRepository,
  dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)

  val genres: LiveData<PagedList<Genre>>
  val indexes: LiveData<List<String>>

  init {
    val genres = repository.allGenres()
    this.genres = genres.factory.paged()
    this.indexes = genres.indexes
  }

  fun reload() {
    networkScope.launch {
      repository.getRemote()
    }
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }
}