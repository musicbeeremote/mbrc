package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ArtistViewModel(
  private val repository: ArtistRepository,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _artists: MediatorLiveData<PagedList<Artist>> = MediatorLiveData()
  val artists: LiveData<PagedList<Artist>>
    get() = _artists

  init {
    var lastSource = repository.allArtists().paged()
    _artists.addSource(lastSource) { data -> _artists.value = data }

    searchModel.search.onEach {
      _artists.removeSource(lastSource)

      val factory = if (it.isEmpty()) repository.getAll() else repository.search(it)
      lastSource = factory.paged()
      _artists.addSource(lastSource) { data -> _artists.value = data }
    }.launchIn(scope)
  }
}