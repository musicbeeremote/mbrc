package com.kelsos.mbrc.features.library.sync

import arrow.core.Either
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.library.sync.SyncCategory.ALBUMS
import com.kelsos.mbrc.features.library.sync.SyncCategory.ARTISTS
import com.kelsos.mbrc.features.library.sync.SyncCategory.GENRES
import com.kelsos.mbrc.features.library.sync.SyncCategory.PLAYLISTS
import com.kelsos.mbrc.features.library.sync.SyncCategory.TRACKS
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncedData
import kotlinx.coroutines.withContext
import timber.log.Timber

class LibrarySyncUseCaseImpl(
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val playlistRepository: PlaylistRepository,
  private val metrics: SyncMetrics,
  private val dispatchers: AppCoroutineDispatchers
) : LibrarySyncUseCase {

  private var running: Boolean = false

  override suspend fun sync(auto: Boolean, progress: SyncProgress): SyncResult {

    if (running) {
      return SyncResult.NOOP
    }
    running = true

    Timber.v("Starting library metadata network")

    metrics.librarySyncStarted()

    val result: SyncResult = if (checkIfShouldSync(auto)) {
      Either.catch {
        genreRepository.getRemote { current, total -> progress(current, total, GENRES) }
        artistRepository.getRemote { current, total -> progress(current, total, ARTISTS) }
        albumRepository.getRemote { current, total -> progress(current, total, ALBUMS) }
        trackRepository.getRemote { current, total -> progress(current, total, TRACKS) }
        playlistRepository.getRemote { current, total -> progress(current, total, PLAYLISTS) }
        true
      }.fold({ SyncResult.FAILED }, { SyncResult.SUCCESS })
    } else {
      SyncResult.NOOP
    }

    withContext(dispatchers.disk) {
      val syncedData = SyncedData(
        genres = genreRepository.count(),
        artists = artistRepository.count(),
        albums = albumRepository.count(),
        tracks = trackRepository.count(),
        playlists = playlistRepository.count()
      )
      metrics.librarySyncComplete(syncedData)
    }

    running = false
    return result
  }

  private suspend fun checkIfShouldSync(auto: Boolean): Boolean = if (auto) {
    isEmpty()
  } else {
    true
  }

  private suspend fun isEmpty(): Boolean {
    return genreRepository.cacheIsEmpty() &&
      artistRepository.cacheIsEmpty() &&
      albumRepository.cacheIsEmpty() &&
      trackRepository.cacheIsEmpty()
  }

  override fun isRunning(): Boolean {
    return running
  }
}