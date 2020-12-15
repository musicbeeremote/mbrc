package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AlbumViewModel(
  private val repository: AlbumRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _albums: MediatorLiveData<PagedList<Album>> = MediatorLiveData()
  val albums: LiveData<PagedList<Album>>
    get() = _albums

  init {
    var lastSource = repository.getAlbumsSorted().paged()
    _albums.addSource(lastSource) { data -> _albums.value = data }

    searchModel.search.onEach {
      _albums.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()
      _albums.addSource(lastSource) { data -> _albums.value = data }
    }.launchIn(scope)
  }
}