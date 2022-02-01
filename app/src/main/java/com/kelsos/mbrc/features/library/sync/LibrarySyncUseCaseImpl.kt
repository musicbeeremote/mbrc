package com.kelsos.mbrc.features.library.sync

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.common.data.cacheIsEmpty
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.CoverCache
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.LibraryRepositories
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.library.sync.SyncCategory.ALBUMS
import com.kelsos.mbrc.features.library.sync.SyncCategory.ARTISTS
import com.kelsos.mbrc.features.library.sync.SyncCategory.COVERS
import com.kelsos.mbrc.features.library.sync.SyncCategory.GENRES
import com.kelsos.mbrc.features.library.sync.SyncCategory.PLAYLISTS
import com.kelsos.mbrc.features.library.sync.SyncCategory.TRACKS
import com.kelsos.mbrc.features.playlists.PlaylistRepository
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.networking.client.ConnectivityVerifier
import kotlinx.coroutines.withContext
import timber.log.Timber

fun <L : Any, R : Any> Either<L, R>.orFailed(): Either<SyncResult, R> {
  return mapLeft { SyncResult.FAILED }
}

fun Boolean.orNoOp(): Either<SyncResult, Boolean> {
  return if (!this) this.right() else SyncResult.NOOP.left()
}

suspend fun <T : Any> Repository<T>.fetch(
  progress: SyncProgress,
  category: Int
): Either<SyncResult, Unit> = getRemote { current, total ->
  progress(
    current,
    total,
    category
  )
}.orFailed()

class LibrarySyncUseCaseImpl(
  libraryRepositories: LibraryRepositories,
  private val playlistRepository: PlaylistRepository,
  private val connectivityVerifier: ConnectivityVerifier,
  private val metrics: SyncMetrics,
  private val coverCache: CoverCache,
  private val dispatchers: AppCoroutineDispatchers
) : LibrarySyncUseCase {
  private val genreRepository: GenreRepository = libraryRepositories.genreRepository
  private val artistRepository: ArtistRepository = libraryRepositories.artistRepository
  private val albumRepository: AlbumRepository = libraryRepositories.albumRepository
  private val trackRepository: TrackRepository = libraryRepositories.trackRepository

  private var running: Boolean = false

  override suspend fun sync(auto: Boolean, progress: SyncProgress): SyncResult {
    val result = either<SyncResult, Unit> {
      running.orNoOp().bind()
      running = true
      Timber.v("Starting library metadata sync")
      connectivityVerifier.verify().orFailed().bind()
      metrics.librarySyncStarted()
      checkIfShouldSync(auto).bind()
      genreRepository.fetch(progress, GENRES).bind()
      artistRepository.fetch(progress, ARTISTS).bind()
      albumRepository.fetch(progress, ALBUMS).bind()
      trackRepository.fetch(progress, TRACKS).bind()
      playlistRepository.fetch(progress, PLAYLISTS).bind()
      coverCache.cache { current, total -> progress(current, total, COVERS) }.orFailed().bind()
    }.fold({
      it
    }, {
      SyncResult.SUCCESS
    })

    if (result == SyncResult.FAILED) {
      metrics.librarySyncFailed()
    } else {
      withContext(dispatchers.io) {
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

  private suspend fun checkIfShouldSync(auto: Boolean): Either<SyncResult, Boolean> = if (auto)
    if (isEmpty()) true.right() else SyncResult.NOOP.left()
  else
    true.right()

  private suspend fun isEmpty(): Boolean {
    return genreRepository.cacheIsEmpty() &&
      artistRepository.cacheIsEmpty() &&
      albumRepository.cacheIsEmpty() &&
      trackRepository.cacheIsEmpty()
  }

  override fun isRunning(): Boolean = running
}
