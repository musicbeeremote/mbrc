package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.library.albums.AlbumRepository
import com.kelsos.mbrc.library.artists.ArtistRepository
import com.kelsos.mbrc.library.genres.GenreRepository
import com.kelsos.mbrc.playlists.PlaylistRepository
import com.kelsos.mbrc.library.tracks.TrackRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4

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

  private var disposable: Disposable? = null
  private var running: Boolean = false
  private var onCompleteListener: LibrarySyncInteractor.OnCompleteListener? = null

  override fun sync(auto: Boolean) {
    disposable?.let {
      if (!it.isDisposed) {
        return
      }
    }
    running = true

    Timber.v("Starting library metadata sync")

    disposable = checkIfShouldSync(auto)
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
        trackRepository.cacheIsEmpty(),
        Function4 { noGenres, noArtists, noAlbums, noTracks ->
          noGenres && noArtists && noAlbums && noTracks
        })
  }

  override fun setOnCompleteListener(onCompleteListener: LibrarySyncInteractor.OnCompleteListener?) {
    this.onCompleteListener = onCompleteListener
  }

  override fun isRunning(): Boolean {
    return running
  }

  inner class ShouldNotProceedException : Exception()
}
