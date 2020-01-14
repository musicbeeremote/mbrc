package com.kelsos.mbrc.features.library.tracks

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow

class TrackViewModel(repository: TrackRepository) : BaseViewModel<LibraryResult>() {
  val tracks: Flow<PagingData<Track>> = repository.getAll().cachedIn(viewModelScope)
}
