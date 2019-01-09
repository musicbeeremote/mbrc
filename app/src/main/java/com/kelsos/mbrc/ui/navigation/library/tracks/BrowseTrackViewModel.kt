package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BrowseTrackViewModel(
  private val repository: TrackRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)
  val tracks: LiveData<PagedList<TrackEntity>>
  val indexes: LiveData<List<String>>

  init {
    val allTracks = repository.allTracks()
    tracks = allTracks.factory.paged()
    indexes = allTracks.indexes
  }


  fun reload() {
    networkScope.launch(dispatchers.network) {
      repository.getRemote()
    }
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }
}