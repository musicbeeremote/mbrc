package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.TrackRepository
import rx.Scheduler
import rx.Subscription
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LibraryPresenterImpl
@Inject constructor(private val genreRepository: GenreRepository,
                    private val artistRepository: ArtistRepository,
                    private val albumRepository: AlbumRepository,
                    private val trackRepository: TrackRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler,
                    private val bus: RxBus) : LibraryPresenter, BasePresenter<LibraryView>() {

  private var subscription: Subscription? = null
  private var running: Boolean = false;

  override fun refresh() {
    view?.showRefreshing()
    subscription?.unsubscribe()

    running = true

    subscription = genreRepository.getRemote()
        .andThen(artistRepository.getRemote())
        .andThen(albumRepository.getRemote())
        .andThen(trackRepository.getRemote())
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .doOnTerminate {
          view?.hideRefreshing()
          running = false
        }
        .subscribe({
          bus.post(LibraryRefreshCompleteEvent())
          Timber.v("Library refresh was complete")
        }) {
          Timber.e(it, "Refresh couldn't complete")
          view?.refreshFailed()
        }
  }

  override fun attach(view: LibraryView) {
    super.attach(view)
    if (!running) {
      view.hideRefreshing()
    }
  }
}

