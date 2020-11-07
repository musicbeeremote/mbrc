package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.repositories.AlbumRepository

class AlbumViewModel(repository: AlbumRepository) : ViewModel() {
  val albums: LiveData<PagedList<Album>> = repository.getAlbumsSorted().paged()
}