package com.kelsos.mbrc.content.sync

import arrow.core.Try
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
  private val dispatchers: AppCoroutineDispatchers
) : LibrarySyncUseCase {

  private var running: Boolean = false

  override suspend fun sync(auto: Boolean): SyncResult {

    if (running) {
      return SyncResult.NOOP
    }
    running = true

    Timber.v("Starting library metadata network")

    metrics.librarySyncStarted()

    val result: SyncResult = if (checkIfShouldSync(auto)) {
      Try {
        genreRepository.getRemote()
        artistRepository.getRemote()
        albumRepository.getRemote()
        trackRepository.getRemote()
        playlistRepository.getRemote()
        return@Try true
      }.toEither().fold({ SyncResult.FAILED }, { SyncResult.SUCCESS })
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