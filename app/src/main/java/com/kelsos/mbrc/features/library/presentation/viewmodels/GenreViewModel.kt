package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.LibrarySearchModel
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge

class GenreViewModel(
  private val repository: GenreRepository,
  searchModel: LibrarySearchModel
) : BaseViewModel<UiMessageBase>() {
  @OptIn(FlowPreview::class)
  val genres: Flow<PagingData<Genre>> = searchModel.search.flatMapMerge { keyword ->
    if (keyword.isEmpty()) {
      repository.getAll()
    } else {
      repository.search(keyword)
    }
  }.cachedIn(viewModelScope)
}
