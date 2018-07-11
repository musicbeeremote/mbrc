package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.Sorting
import com.kelsos.mbrc.preferences.AlbumSortingStore
import com.kelsos.mbrc.utilities.AppRxSchedulers

class BrowseAlbumViewModel(
  private val repository: AlbumRepository,
  private val albumSortingStore: AlbumSortingStore,
  private val appRxSchedulers: AppRxSchedulers
) : ViewModel() {

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