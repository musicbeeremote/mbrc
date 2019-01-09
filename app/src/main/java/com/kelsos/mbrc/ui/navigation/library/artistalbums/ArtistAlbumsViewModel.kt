package com.kelsos.mbrc.ui.navigation.library.artistalbums

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.utilities.paged


class ArtistAlbumsViewModel(
  private val repository: AlbumRepository
) : ViewModel() {

  val albums: LiveData<PagedList<AlbumEntity>> = repository.getAlbumsSorted().factory.paged()
}