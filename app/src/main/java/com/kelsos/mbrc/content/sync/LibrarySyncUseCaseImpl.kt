package com.kelsos.mbrc.content.sync

import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.covers.CoverCache
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class LibrarySyncUseCaseImpl(
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val playlistRepository: PlaylistRepository,
  private val metrics: SyncMetrics,
  private val coverCache: CoverCache,
  dispatchers: AppCoroutineDispatchers,
) : LibrarySyncUseCase {

  private var running: Boolean = false
  private var onCompleteListener: LibrarySyncUseCase.OnCompleteListener? = null
  private var onStartListener: LibrarySyncUseCase.OnStartListener? = null

  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.network)

  override suspend fun sync(auto: Boolean) {
    if (isRunning()) {
      Timber.v("Sync is already running")
      return
    }

    running = true
    scope.launch {
      Timber.v("Starting library metadata sync")
      metrics.librarySyncStarted()
      onStartListener?.onStart()

      val shouldSync = checkIfShouldSync(auto)
      if (!shouldSync) {
        onCompleteListener?.onTermination()
        running = false
        return@launch
      }

      try {
        genreRepository.getRemote()
        artistRepository.getRemote()
        albumRepository.getRemote()
        trackRepository.getRemote()
        playlistRepository.getRemote()
        coverCache.cache()

        val stats = SyncedData(
          genres = genreRepository.count(),
          artists = artistRepository.count(),
          albums = albumRepository.count(),
          tracks = trackRepository.count(),
          playlists = playlistRepository.count()
        )
        onCompleteListener?.onSuccess(stats)
        metrics.librarySyncComplete(stats)
        running = false
      } catch (e: Exception) {
        Timber.e(e, "Refresh couldn't complete")
        metrics.librarySyncFailed()
        onCompleteListener?.onFailure(e)
      } finally {
        running = false
        onCompleteListener?.onTermination()
      }
    }
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

  private suspend fun checkIfShouldSync(auto: Boolean): Boolean {
    return if (auto) isEmpty() else true
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

  override fun setOnStartListener(onStartListener: LibrarySyncUseCase.OnStartListener?) {
    this.onStartListener = onStartListener
  }

  override fun isRunning(): Boolean = running
}
