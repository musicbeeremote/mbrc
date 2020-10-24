package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.PlaylistRepository
import com.kelsos.mbrc.repository.TrackRepository
import rx.Completable
import rx.Scheduler
import rx.Single
import rx.Subscription
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LibrarySyncInteractorImpl
@Inject constructor(
    private val genreRepository: GenreRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("main") private val mainScheduler: Scheduler,
    private val bus: RxBus
) : LibrarySyncInteractor {

  private var subscription: Subscription? = null
  private var running: Boolean = false
  private var onCompleteListener: LibrarySyncInteractor.OnCompleteListener? = null
  private var onStartListener: LibrarySyncInteractor.OnStartListener? = null

  override fun sync(auto: Boolean) {
    if (subscription != null && !subscription!!.isUnsubscribed) {
      return
    }
    running = true

    Timber.v("Starting library metadata sync")
    
    onStartListener?.onStart()

    subscription = checkIfShouldSync(auto)
        .andThen(genreRepository.getRemote())
        .andThen(artistRepository.getRemote())
        .andThen(albumRepository.getRemote())
        .andThen(trackRepository.getRemote())
        .andThen(playlistRepository.getRemote())
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .doOnTerminate {
          onCompleteListener?.onTermination()
          bus.post(LibraryRefreshCompleteEvent())
          running = false
        }
        .subscribe({
          onCompleteListener?.onSuccess()
          Timber.v("Library refresh was complete")
        }) {
          Timber.e(it, "Refresh couldn't complete")
          onCompleteListener?.onFailure(it)
        }
  }

  private fun checkIfShouldSync(auto: Boolean): Completable {
    if (auto) {
      return isEmpty().flatMap {
        if (it) {
          return@flatMap Single.just(it)
        } else {
          return@flatMap Single.error<Boolean>(ShouldNotProceedException())
        }
      }.toCompletable()
    } else {
      return Completable.complete()
    }
  }

  private fun isEmpty(): Single<Boolean> {
    return Single.zip(genreRepository.cacheIsEmpty(),
        artistRepository.cacheIsEmpty(),
        albumRepository.cacheIsEmpty(),
        trackRepository.cacheIsEmpty()
    ) { noGenres, noArtists, noAlbums, noTracks ->
      noGenres && noArtists && noAlbums && noTracks
    }
  }

  override fun setOnCompleteListener(onCompleteListener: LibrarySyncInteractor.OnCompleteListener?) {
    this.onCompleteListener = onCompleteListener
  }

  override fun setOnStartListener(onStartListener: LibrarySyncInteractor.OnStartListener?) {
    this.onStartListener = onStartListener;
  }

  override fun isRunning(): Boolean {
    return running
  }

  inner class ShouldNotProceedException : Exception()
}
