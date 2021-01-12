package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackViewModel(
  private val repository: TrackRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _tracks: MediatorLiveData<PagedList<Track>> = MediatorLiveData()
  val tracks: LiveData<PagedList<Track>>
    get() = _tracks

  init {
    var lastSource = repository.getAll().paged()
    _tracks.addSource(lastSource) { data -> _tracks.value = data }

    searchModel.search.drop(1).onEach {
      _tracks.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()
      _tracks.addSource(lastSource) { data -> _tracks.value = data }
    }.launchIn(scope)
  }
}
