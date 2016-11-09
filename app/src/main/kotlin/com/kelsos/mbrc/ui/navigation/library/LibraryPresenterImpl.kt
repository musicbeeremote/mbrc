package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.TrackRepository
import rx.Scheduler
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

  override fun refresh() {
    view?.showRefreshing()

    addSubcription(genreRepository.getRemote()
        .andThen(artistRepository.getRemote())
        .andThen(albumRepository.getRemote())
        .andThen(trackRepository.getRemote())
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .doOnTerminate {
          view?.hideRefreshing()
        }
        .subscribe({
          bus.post(LibraryRefreshCompleteEvent())
          Timber.v("Library refresh was complete")
        }) {
          view?.refreshFailed()
        })
  }
}
