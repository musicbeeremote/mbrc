package com.kelsos.mbrc.content.sync

import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import timber.log.Timber


class LibrarySyncUseCaseImpl

constructor(
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val playlistRepository: PlaylistRepository,
  private val metrics: SyncMetrics,
  private val appRxSchedulers: AppRxSchedulers
) : LibrarySyncUseCase {

  private var disposable: Disposable? = null
  private var running: Boolean = false
  private var onCompleteListener: LibrarySyncUseCase.OnCompleteListener? = null

  override fun sync(auto: Boolean) {
    disposable?.let {
      if (!it.isDisposed) {
        return
      }
    }
    running = true

    Timber.v("Starting library metadata network")

    metrics.librarySyncStarted()

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
    }.subscribeOn(appRxSchedulers.network)
      .observeOn(appRxSchedulers.main)
      .doOnTerminate {
        onCompleteListener?.onTermination()
        running = false
      }
      .subscribe({
        onCompleteListener?.onSuccess()
        async(CommonPool) {
          val syncedData = SyncedData(
            genres = genreRepository.count(),
            artists = artistRepository.count(),
            albums = albumRepository.count(),
            tracks = trackRepository.count(),
            playlists = playlistRepository.count()
          )
          metrics.librarySyncComplete(syncedData)
        }

      }) {
        Timber.e(it, "Refresh couldn't complete")
        onCompleteListener?.onFailure(it)
        metrics.librarySyncFailed()
      }
  }

  private fun checkIfShouldSync(auto: Boolean): Single<Boolean> {
    return if (auto) {
      isEmpty()
    } else {
      Single.just(true)
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

  override fun setOnCompleteListener(
    onCompleteListener: LibrarySyncUseCase.OnCompleteListener?
  ) {
    this.onCompleteListener = onCompleteListener
  }

  override fun isRunning(): Boolean {
    return running
  }

  inner class ShouldNotProceedException : Exception()
}