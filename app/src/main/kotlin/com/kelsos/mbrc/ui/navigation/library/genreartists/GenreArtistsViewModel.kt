package com.kelsos.mbrc.ui.navigation.library.genreartists

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import kotlinx.coroutines.flow.Flow

class GenreArtistsViewModel(private val repository: ArtistRepository) : ViewModel() {
  private lateinit var artists: Flow<PagingData<ArtistEntity>>
}
