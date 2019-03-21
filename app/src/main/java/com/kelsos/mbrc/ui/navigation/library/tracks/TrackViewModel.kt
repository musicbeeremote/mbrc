package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import kotlinx.coroutines.flow.Flow

class TrackViewModel(
  repository: TrackRepository
) : ViewModel() {
  val tracks: Flow<PagingData<Track>> = repository.getAll().cachedIn(viewModelScope)
}
