package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BrowseTrackViewModel(
  private val repository: TrackRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  val tracks: Flow<PagingData<Track>> = repository.getAll()

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}
