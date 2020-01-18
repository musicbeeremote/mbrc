package com.kelsos.mbrc.features.library.artists

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.features.library.LibraryResult
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged

class ArtistViewModel(
  repository: ArtistRepository,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryResult>(dispatchers) {

  val artists: LiveData<PagedList<Artist>>
  val indexes: LiveData<List<String>>

  init {
    val data = repository.allArtists()
    artists = data.factory.paged()
    indexes = data.indexes
  }
}