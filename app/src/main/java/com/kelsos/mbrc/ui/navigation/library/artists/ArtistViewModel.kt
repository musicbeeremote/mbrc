package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import kotlinx.coroutines.flow.Flow

class ArtistViewModel(
  repository: ArtistRepository,
) : ViewModel() {

  val artists: Flow<PagingData<Artist>> = repository.getAll().cachedIn(viewModelScope)
}
