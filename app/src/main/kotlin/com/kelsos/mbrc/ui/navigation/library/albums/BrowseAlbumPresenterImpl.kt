package com.kelsos.mbrc.ui.navigation.library.albums

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.Sorting
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.AlbumSortingStore
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class BrowseAlbumPresenterImpl
@Inject
constructor(
    private val bus: RxBus,
    private val repository: AlbumRepository,
    private val albumSortingStore: AlbumSortingStore,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<BrowseAlbumView>(),
    BrowseAlbumPresenter {

  private lateinit var albums: LiveData<PagedList<AlbumEntity>>

  private fun observeAlbums(it: DataSource.Factory<Int, AlbumEntity>) {

    if (::albums.isInitialized) {
      albums.removeObservers(this)
    }

    albums = it.paged()
    albums.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun attach(view: BrowseAlbumView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    disposables += repository.getAlbumsSorted()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
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
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          observeAlbums(it)
          view().hideLoading()
        }) {
          Timber.v(it)
          view().hideLoading()
        }
  }

  override fun sortBy(@Sorting.Fields selection: Int) {
    albumSortingStore.setSortingSelection(selection)
    val ascending = albumSortingStore.getSortingOrder() == Sorting.ORDER_ASCENDING
    loadSorted(selection, ascending)
  }

  override fun reload() {
    disposables += repository.getAndSaveRemote()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          observeAlbums(it)
          view().hideLoading()
        }) {
          Timber.v(it)
          view().hideLoading()
        }

  }
}
