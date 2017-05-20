package com.kelsos.mbrc.ui.navigation.library.albums

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.AlbumRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrowseAlbumPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val repository: AlbumRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<BrowseAlbumView>(),
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
    view?.showLoading()
    addDisposable(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view?.update(it)
      view?.hideLoading()
    }) {
      Timber.v(it)
      view?.hideLoading()
    })
  }

  override fun reload() {
    view?.showLoading()
    addDisposable(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      view?.update(it)
      view?.hideLoading()
    }) {
      Timber.v(it)
      view?.hideLoading()
    })

  }

  private fun schedule(it: Single<FlowCursorList<Album>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)
}
