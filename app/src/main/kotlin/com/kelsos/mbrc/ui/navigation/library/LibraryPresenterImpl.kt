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
                    private val bus: RxBus) : LibraryPresenter, BasePresenter<LibraryView>() {

  override fun refresh() {
    addSubcription(genreRepository.getRemote()
        .andThen(artistRepository.getRemote())
        .andThen(albumRepository.getRemote())
        .andThen(trackRepository.getRemote())
        .subscribeOn(ioScheduler)
        .subscribe({
          bus.post(LibraryRefreshCompleteEvent())
          Timber.v("Library refresh was complete")
        }) {
          view?.refreshFailed()
        })
  }
}
