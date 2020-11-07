package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.repositories.ArtistRepository

class ArtistViewModel(repository: ArtistRepository) : ViewModel() {
  val artists: LiveData<PagedList<Artist>> = repository.allArtists().paged()
}