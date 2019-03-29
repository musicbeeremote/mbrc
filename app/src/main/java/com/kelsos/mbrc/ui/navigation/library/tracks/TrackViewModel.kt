package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch

class TrackViewModel(
  private val repository: TrackRepository,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryResult>(dispatchers) {

  val tracks: LiveData<PagedList<Track>>
  val indexes: LiveData<List<String>>

  init {
    val allTracks = repository.allTracks()
    tracks = allTracks.factory.paged()
    indexes = allTracks.indexes
  }

  fun reload() {
    scope.launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}