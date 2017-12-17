package com.kelsos.mbrc.ui.navigation.library.albums

import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.albums.Sorting
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.AlbumSortingStore
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import io.reactivex.Single
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

  override fun attach(view: BrowseAlbumView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    view().showLoading()
    addDisposable(repository.getAlbumsSorted().compose { schedule(it) }.subscribe({
      val liveData = it.paged()
      liveData.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
      view().hideLoading()
    }) {
      Timber.v(it)
      view().hideLoading()
    })
  }

  override fun showSorting() {
    view().showSorting(albumSortingStore.getSortingOrder(), albumSortingStore.getSortingSelection())
  }

  override fun order(@Sorting.Order order: Long) {
    albumSortingStore.setSortingOrder(order)

    val ascending = order == Sorting.ORDER_ASCENDING
    val sortingSelection = albumSortingStore.getSortingSelection()
    loadSorted(sortingSelection, ascending)
  }

  private fun loadSorted(sortingSelection: Long, ascending: Boolean) {
    addDisposable(repository.getAlbumsSorted(sortingSelection, ascending)
        .compose { schedule(it) }
        .subscribe({
          val liveData = it.paged()
          liveData.observe(this, Observer {
            if (it != null) {
              view().update(it)
            }
          })
          view().hideLoading()
        }) {
          Timber.v(it)
          view().hideLoading()
        })
  }


  override fun sortBy(@Sorting.Fields selection: Long) {
    albumSortingStore.setSortingSelection(selection)
    val ascending = albumSortingStore.getSortingOrder() == Sorting.ORDER_ASCENDING
    loadSorted(selection, ascending)
  }

  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      val pagedList = it.paged()
      pagedList.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })

      view().hideLoading()
    }) {
      Timber.v(it)
      view().hideLoading()
    })

  }

  private fun schedule(it: Single<DataSource.Factory<Int, AlbumEntity>>) = it.observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())
}
