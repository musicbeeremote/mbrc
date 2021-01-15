package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackViewModelImpl(
  private val repository: TrackRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : TrackViewModel(dispatchers) {
  override val tracks = MediatorLiveData<PagedList<Track>>()

  init {
    var lastSource = repository.getAll().paged()
    tracks.addSource(lastSource) { data -> tracks.value = data }

    searchModel.search.drop(1).onEach {
      tracks.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()
      tracks.addSource(lastSource) { data -> tracks.value = data }
    }.launchIn(scope)
  }
}
