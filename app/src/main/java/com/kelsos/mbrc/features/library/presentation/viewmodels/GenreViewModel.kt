package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GenreViewModel(
  private val repository: GenreRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _genres: MediatorLiveData<PagedList<Genre>> = MediatorLiveData()
  val genres: LiveData<PagedList<Genre>>
    get() = _genres

  init {
    var lastSource: LiveData<PagedList<Genre>> = repository.getAll().paged()
    _genres.addSource(lastSource) { data -> _genres.value = data }

    searchModel.search.onEach {
      _genres.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()

      _genres.addSource(lastSource) { data -> _genres.value = data }
    }.launchIn(scope)
  }
}