package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AlbumViewModelImpl(
  private val repository: AlbumRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : AlbumViewModel(dispatchers) {
  override val albums = MediatorLiveData<PagedList<Album>>()

  init {
    var lastSource = repository.getAlbumsSorted().paged()
    albums.addSource(lastSource) { data -> albums.value = data }

    searchModel.search.drop(1).onEach {
      albums.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()
      albums.addSource(lastSource) { data -> albums.value = data }
    }.launchIn(scope)
  }
}
