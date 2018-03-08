package com.kelsos.mbrc.content.sync

import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4
import timber.log.Timber
import javax.inject.Inject

class LibrarySyncInteractorImpl
@Inject constructor(
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val playlistRepository: PlaylistRepository,
  private val schedulerProvider: SchedulerProvider,
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

    val start = System.currentTimeMillis()

    disposable = checkIfShouldSync(auto).flatMapCompletable { empty ->
      if (empty) {
        return@flatMapCompletable genreRepository.getRemote()
            .andThen(artistRepository.getRemote())
            .andThen(albumRepository.getRemote())
            .andThen(trackRepository.getRemote())
            .andThen(playlistRepository.getRemote())
      } else {
        return@flatMapCompletable Completable.error(ShouldNotProceedException())
      }
    }.subscribeOn(schedulerProvider.sync())
        .observeOn(schedulerProvider.main())
        .doOnTerminate {
          onCompleteListener?.onTermination()
          bus.post(LibraryRefreshCompleteEvent())
          running = false
        }
        .subscribe({
          onCompleteListener?.onSuccess()
          Timber.v("Library refresh was complete after ${System.currentTimeMillis() - start} ms")
        }) {
          Timber.e(it, "Refresh couldn't complete")
          onCompleteListener?.onFailure(it)
        }
  }

  private fun checkIfShouldSync(auto: Boolean): Single<Boolean> {
    if (auto) {
      return isEmpty()
    } else {
      return Single.just(true)
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