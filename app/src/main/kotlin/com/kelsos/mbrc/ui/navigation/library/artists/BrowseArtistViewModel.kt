package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BrowseArtistViewModel(
  private val repository: ArtistRepository,
) : ViewModel() {

  val artists: Flow<PagingData<Artist>> = repository.getAll()

  fun reload() {
    viewModelScope.launch { repository.getRemote() }
  }
}
