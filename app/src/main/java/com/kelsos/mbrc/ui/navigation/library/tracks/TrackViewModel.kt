package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import kotlinx.coroutines.flow.Flow

class TrackViewModel(repository: TrackRepository) : BaseViewModel<LibraryResult>() {
  val tracks: Flow<PagingData<Track>> = repository.getAll().cachedIn(viewModelScope)
}
