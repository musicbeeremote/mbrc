package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class BrowseAlbumViewModel(
  private val repository: AlbumRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)

  val albums: MediatorLiveData<PagedList<AlbumEntity>> = MediatorLiveData()
  val indexes: MediatorLiveData<List<String>> = MediatorLiveData()
}