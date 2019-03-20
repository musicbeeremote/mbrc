package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ArtistViewModel(
  private val repository: ArtistRepository,
  dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)

  val artists: LiveData<PagedList<Artist>>
  val indexes: LiveData<List<String>>

  init {
    val data = repository.allArtists()
    artists = data.factory.paged()
    indexes = data.indexes
  }

  fun reload() {
    networkScope.launch { repository.getRemote() }
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }
}