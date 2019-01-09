package com.kelsos.mbrc.content.sync

import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
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
  private var onCompleteListener: LibrarySyncUseCase.OnCompleteListener? = null

  override suspend fun sync(auto: Boolean) {
    if (running) {
      return
    }
    running = true

    Timber.v("Starting library metadata network")

    metrics.librarySyncStarted()

    if (checkIfShouldSync(auto)) {
      try {
        genreRepository.getRemote()
        artistRepository.getRemote()
        albumRepository.getRemote()
        trackRepository.getRemote()
        playlistRepository.getRemote()
      } catch (ex: Exception) {
        onCompleteListener?.onTermination()
        onCompleteListener?.onFailure(ex)
        metrics.librarySyncFailed()

        return
      }
    }

    onCompleteListener?.onSuccess()
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
  }

  private suspend fun checkIfShouldSync(auto: Boolean): Boolean {
    return if (auto) {
      isEmpty()
    } else {
      true
    }
  }

  private suspend fun isEmpty(): Boolean {
    return genreRepository.cacheIsEmpty() &&
      artistRepository.cacheIsEmpty() &&
      albumRepository.cacheIsEmpty() &&
      trackRepository.cacheIsEmpty()
  }

  override fun setOnCompleteListener(
    onCompleteListener: LibrarySyncUseCase.OnCompleteListener?
  ) {
    this.onCompleteListener = onCompleteListener
  }

  override fun isRunning(): Boolean {
    return running
  }
}