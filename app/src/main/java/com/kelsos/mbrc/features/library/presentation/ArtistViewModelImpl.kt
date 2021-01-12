package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.preferences.SettingsManager
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ArtistViewModelImpl(
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  searchModel: LibrarySearchModel,
  dispatchers: AppCoroutineDispatchers
) : ArtistViewModel(dispatchers) {
  override val artists = MediatorLiveData<PagedList<Artist>>()

  init {
    scope.launch {
      var lastSource = getSource(settingsManager.onlyAlbumArtists().first()).paged()
      artists.addSource(lastSource) { data -> artists.value = data }

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
    artists.removeSource(lastSource)

    val factory = if (keyword.isEmpty()) {
      getSource(displayAlbumArtists)
    } else {
      repository.search(keyword)
    }
    val liveData = factory.paged()
    artists.addSource(liveData) { data -> artists.value = data }
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
