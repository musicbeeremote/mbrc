package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AlbumViewModel(
  private val repository: AlbumRepository,
  dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val job: Job = Job()
  private val scope = CoroutineScope(dispatchers.network + job)

  val albums: LiveData<PagedList<Album>>
  val indexes: LiveData<List<String>>

  init {
    val model = repository.getAlbumsSorted()
    albums = model.factory.paged()
    indexes = model.indexes
  }

  fun reload() {
    scope.launch {
      repository.getRemote()
    }
  }

  override fun onCleared() {
    job.cancel()
    super.onCleared()
  }
}