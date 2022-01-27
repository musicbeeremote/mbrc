package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge

class TrackViewModel(
  private val repository: TrackRepository,
  searchModel: LibrarySearchModel,
) : BaseViewModel<UiMessageBase>() {
  val tracks: Flow<PagingData<Track>> =
    searchModel.search
      .flatMapMerge { keyword ->
        if (keyword.isEmpty()) {
          repository.getAll()
        } else {
          repository.search(keyword)
        }
      }.cachedIn(viewModelScope)
}
