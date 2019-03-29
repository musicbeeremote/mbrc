package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import kotlinx.coroutines.flow.Flow

class ArtistViewModel(repository: ArtistRepository) : BaseViewModel<LibraryResult>() {
  val artists: Flow<PagingData<Artist>> = repository.getAll().cachedIn(viewModelScope)
}
