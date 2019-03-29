package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch

class ArtistViewModel(
  private val repository: ArtistRepository,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryResult>(dispatchers) {

  val artists: LiveData<PagedList<Artist>>
  val indexes: LiveData<List<String>>

  init {
    val data = repository.allArtists()
    artists = data.factory.paged()
    indexes = data.indexes
  }

  fun reload() {
    scope.launch { repository.getRemote() }
  }
}