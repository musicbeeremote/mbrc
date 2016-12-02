package com.kelsos.mbrc.ui.navigation.library.artists

import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.library.ArtistRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Scheduler
import rx.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrowseArtistPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val repository: ArtistRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<BrowseArtistView>(),
    BrowseArtistPresenter {

  override fun attach(view: BrowseArtistView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    addSubcription(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error while loading the data from the database")
    }))

  }

  override fun reload() {
    addSubcription(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error retrieving the data")
    }))
  }

  private fun schedule(it: Single<FlowCursorList<Artist>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)

}
