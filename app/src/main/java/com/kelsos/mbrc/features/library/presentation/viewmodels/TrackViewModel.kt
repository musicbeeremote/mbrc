package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import kotlinx.coroutines.flow.Flow

class TrackViewModel(repository: TrackRepository) : ViewModel() {
  val tracks: Flow<PagingData<Track>> = repository.getAll().cachedIn(viewModelScope)
}
