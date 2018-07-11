package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.experimental.launch

class BrowseTrackViewModel(
  private val repository: TrackRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  val tracks: LiveData<PagedList<TrackEntity>>
  val indexes: LiveData<List<String>>

  init {
    val allTracks = repository.allTracks()
    tracks = allTracks.factory.paged()
    indexes = allTracks.indexes
  }


  fun reload() {
    launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}