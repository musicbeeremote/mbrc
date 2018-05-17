package com.kelsos.mbrc.ui.navigation.library.albums

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.AlbumsModel
import com.kelsos.mbrc.content.library.albums.Sorting
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.AlbumSortingStore
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class BrowseAlbumPresenterImpl
@Inject
constructor(
  private val repository: AlbumRepository,
  private val albumSortingStore: AlbumSortingStore,
  private val appRxSchedulers: AppRxSchedulers
) : BasePresenter<BrowseAlbumView>(),
  BrowseAlbumPresenter {

  private lateinit var albums: LiveData<PagedList<AlbumEntity>>
  private lateinit var indexes: LiveData<List<String>>

  private fun observeAlbums(model: AlbumsModel) {

    if (::albums.isInitialized) {
      albums.removeObservers(this)
    }

    albums = model.factory.paged()
    albums.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })

    indexes = model.indexes

    model.indexes.observe(this, Observer {
      if (it == null) {
        return@Observer
      }
      view().updateIndexes(it)
    })
  }

  override fun load() {
    disposables += repository.getAlbumsSorted()
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().hideLoading() }
      .subscribe({
        observeAlbums(it)
        view().hideLoading()
      }) {
        Timber.v(it)
        view().hideLoading()
      }
  }

  override fun showSorting() {
    view().showSorting(albumSortingStore.getSortingOrder(), albumSortingStore.getSortingSelection())
  }

  override fun order(@Sorting.Order order: Int) {
    albumSortingStore.setSortingOrder(order)

    val ascending = order == Sorting.ORDER_ASCENDING
    val sortingSelection = albumSortingStore.getSortingSelection()
    loadSorted(sortingSelection, ascending)
  }

  private fun loadSorted(sortingSelection: Int, ascending: Boolean) {
    disposables += repository.getAlbumsSorted(sortingSelection, ascending)
      .observeOn(appRxSchedulers.main)
      .subscribeOn(appRxSchedulers.database)
      .doFinally { view().hideLoading() }
      .subscribe({
        observeAlbums(it)
      }) {
        Timber.v(it)
      }
  }

  override fun sortBy(@Sorting.Fields selection: Int) {
    albumSortingStore.setSortingSelection(selection)
    val ascending = albumSortingStore.getSortingOrder() == Sorting.ORDER_ASCENDING
    loadSorted(selection, ascending)
  }

  override fun reload() {
    disposables += repository.getRemote()
      .subscribeOn(appRxSchedulers.network)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().hideLoading() }
      .subscribe({

      }) {
        Timber.v(it)
      }
  }
}