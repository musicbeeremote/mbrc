package com.kelsos.mbrc.content.sync

import arrow.core.Either
import arrow.core.Try
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.covers.CoverCache
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LibrarySyncUseCaseImpl(
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val playlistRepository: PlaylistRepository,
  private val metrics: SyncMetrics,
  private val coverCache: CoverCache,
  private val dispatchers: AppCoroutineDispatchers
) : LibrarySyncUseCase {

  private var running: Boolean = false
  private var onCompleteListener: LibrarySyncUseCase.OnCompleteListener? = null
  private var onStartListener: LibrarySyncUseCase.OnStartListener? = null

  override suspend fun sync(auto: Boolean): Either<Throwable, Boolean> {

    if (isRunning()) {
      Timber.v("Sync is already running")
      return Either.right(false)
    }

    running = true
    Timber.v("Starting library metadata sync")
    metrics.librarySyncStarted()
    onStartListener?.onStart()

    val result: Either<Throwable, Boolean> = if (checkIfShouldSync(auto)) {
      Try {
        genreRepository.getRemote()
        artistRepository.getRemote()
        albumRepository.getRemote()
        trackRepository.getRemote()
        playlistRepository.getRemote()
        coverCache.cache()

        onCompleteListener?.onSuccess(syncStats())
        metrics.librarySyncComplete(syncStats())

        return@Try true
      }.toEither()
    } else {
      Either.right(false)
    }

    if (result.isLeft()) {
      metrics.librarySyncFailed()
    } else {
      withContext(dispatchers.disk) {
        metrics.librarySyncComplete(syncStats())
        onCompleteListener?.onSuccess(syncStats())
      }
    }

    onCompleteListener?.onTermination()
    running = false
    return result
  }

  override suspend fun syncStats(): SyncedData {
    return SyncedData(
      genres = genreRepository.count(),
      artists = artistRepository.count(),
      albums = albumRepository.count(),
      tracks = trackRepository.count(),
      playlists = playlistRepository.count()
    )
  }

  private suspend fun checkIfShouldSync(auto: Boolean): Boolean = if (auto)
    isEmpty()
  else
    true

  private suspend fun isEmpty(): Boolean {
    return genreRepository.cacheIsEmpty() &&
      artistRepository.cacheIsEmpty() &&
      albumRepository.cacheIsEmpty() &&
      trackRepository.cacheIsEmpty()
  }

  override fun setOnCompleteListener(onCompleteListener: LibrarySyncUseCase.OnCompleteListener?) {
    this.onCompleteListener = onCompleteListener
  }

  override fun setOnStartListener(onStartListener: LibrarySyncUseCase.OnStartListener?) {
    this.onStartListener = onStartListener
  }

  override fun isRunning(): Boolean = running
}
