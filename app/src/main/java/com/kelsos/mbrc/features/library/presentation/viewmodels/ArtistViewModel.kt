package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ArtistViewModel(
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _artists: MediatorLiveData<PagedList<Artist>> = MediatorLiveData()
  val artists: LiveData<PagedList<Artist>>
    get() = _artists

  init {
    scope.launch {
      var lastSource = getSource(settingsManager.onlyAlbumArtists().first()).paged()
      _artists.addSource(lastSource) { data -> _artists.value = data }

      searchModel.search.drop(1).onEach { keyword ->
        lastSource = updateDataSource(
          lastSource,
          keyword,
          settingsManager.onlyAlbumArtists().first()
        )
      }.launchIn(scope)

      settingsManager.onlyAlbumArtists().drop(1).onEach { onlyAlbumArtists ->
        lastSource = updateDataSource(
          lastSource,
          searchModel.search.first(),
          onlyAlbumArtists
        )
      }.launchIn(scope)
    }
  }

  private fun updateDataSource(
    lastSource: LiveData<PagedList<Artist>>,
    keyword: String,
    displayAlbumArtists: Boolean
  ): LiveData<PagedList<Artist>> {
    _artists.removeSource(lastSource)

    val factory = if (keyword.isEmpty()) {
      getSource(displayAlbumArtists)
    } else {
      repository.search(keyword)
    }
    val liveData = factory.paged()
    _artists.addSource(liveData) { data -> _artists.value = data }
    return liveData
  }

  private fun getSource(displayAlbumArtists: Boolean): DataSource.Factory<Int, Artist> {
    return if (displayAlbumArtists) {
      repository.getAlbumArtistsOnly()
    } else {
      repository.allArtists()
    }
  }
}
