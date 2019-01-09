package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.Sorting
import com.kelsos.mbrc.preferences.AlbumSortingStore
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class BrowseAlbumViewModel(
  private val repository: AlbumRepository,
  private val albumSortingStore: AlbumSortingStore,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  private val viewModelJob: Job = Job()
  private val networkScope = CoroutineScope(dispatchers.network + viewModelJob)

  val albums: MediatorLiveData<PagedList<AlbumEntity>> = MediatorLiveData()
  val indexes: MediatorLiveData<List<String>> = MediatorLiveData()


  init {
    //  albums.addSource(repository.getAlbumsSorted())
  }

  fun showSorting() {
    //view().showSorting(albumSortingStore.getSortingOrder(), albumSortingStore.getSortingSelection())
  }

  fun order(@Sorting.Order order: Int) {
    albumSortingStore.setSortingOrder(order)

    val ascending = order == Sorting.ORDER_ASCENDING
    val sortingSelection = albumSortingStore.getSortingSelection()

  }

  fun sortBy(@Sorting.Fields selection: Int) {
    albumSortingStore.setSortingSelection(selection)
    val ascending = albumSortingStore.getSortingOrder() == Sorting.ORDER_ASCENDING

  }
}