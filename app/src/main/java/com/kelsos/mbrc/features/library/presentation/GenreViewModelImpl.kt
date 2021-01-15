package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GenreViewModelImpl(
  private val repository: GenreRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : GenreViewModel(dispatchers) {
  override val genres = MediatorLiveData<PagedList<Genre>>()

  init {
    var lastSource: LiveData<PagedList<Genre>> = repository.getAll().paged()
    genres.addSource(lastSource) { data -> genres.value = data }

    searchModel.search.drop(1).onEach {
      genres.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()

      genres.addSource(lastSource) { data -> genres.value = data }
    }.launchIn(scope)
  }
}
