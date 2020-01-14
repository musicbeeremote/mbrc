package com.kelsos.mbrc.content.sync

import arrow.core.Try
import com.kelsos.mbrc.covers.CoverCache
import com.kelsos.mbrc.features.library.albums.AlbumRepository
import com.kelsos.mbrc.features.library.artists.ArtistRepository
import com.kelsos.mbrc.features.library.genres.GenreRepository
import com.kelsos.mbrc.features.library.tracks.TrackRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
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

  override suspend fun sync(auto: Boolean): SyncResult {

    if (isRunning()) {
      Timber.v("Sync is already running")
      return SyncResult.NOOP
    }

    running = true
    Timber.v("Starting library metadata sync")

    metrics.librarySyncStarted()

    val result: SyncResult = if (checkIfShouldSync(auto)) {
      Try {
        genreRepository.getRemote()
        artistRepository.getRemote()
        albumRepository.getRemote()
        trackRepository.getRemote()
        playlistRepository.getRemote()
        coverCache.cache()

        metrics.librarySyncComplete(syncStats())

        return@Try true
      }.toEither().fold({ SyncResult.FAILED }, { SyncResult.SUCCESS })
    } else {
      SyncResult.NOOP
    }

    if (result == SyncResult.FAILED) {
      metrics.librarySyncFailed()
    } else {
      withContext(dispatchers.disk) {
        metrics.librarySyncComplete(syncStats())
      }
    }

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

  override fun isRunning(): Boolean = running
}
