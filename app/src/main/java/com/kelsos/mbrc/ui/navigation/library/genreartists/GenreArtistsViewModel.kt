package com.kelsos.mbrc.ui.navigation.library.genreartists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.utilities.AppRxSchedulers

class GenreArtistsViewModel(
  private val repository: ArtistRepository,
  private val appRxSchedulers: AppRxSchedulers
) : ViewModel() {

  private lateinit var artists: LiveData<PagedList<ArtistEntity>>
}