package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.navigation.library.LibraryResult
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch

class AlbumViewModel(
  private val repository: AlbumRepository,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<LibraryResult>(dispatchers) {

  val albums: LiveData<PagedList<Album>>
  val indexes: LiveData<List<String>>

  init {
    val model = repository.getAlbumsSorted()
    albums = model.factory.paged()
    indexes = model.indexes
  }

  fun reload() {
    scope.launch {
      val result = repository.getRemote()
        .toEither()
        .fold({
          LibraryResult.RefreshFailure
        }, {
          LibraryResult.RefreshSuccess
        })
      emit(result)
    }
  }
}