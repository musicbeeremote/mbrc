package com.kelsos.mbrc.features.queue

import arrow.core.Either
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.Meta.Album
import com.kelsos.mbrc.common.Meta.Artist
import com.kelsos.mbrc.common.Meta.Genre
import com.kelsos.mbrc.common.Meta.Track
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.repositories.LibraryRepositories
import com.kelsos.mbrc.features.library.repositories.TrackQuery
import com.kelsos.mbrc.features.player.cover.CoverPayload
import com.kelsos.mbrc.features.queue.Queue.Default
import com.kelsos.mbrc.features.queue.Queue.PlayAlbum
import com.kelsos.mbrc.features.queue.Queue.PlayAll
import com.kelsos.mbrc.features.queue.Queue.PlayArtist
import com.kelsos.mbrc.features.settings.DefaultActionPreferenceStore
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.withContext
import timber.log.Timber

class QueueUseCaseImpl(
  repositories: LibraryRepositories,
  private val settings: DefaultActionPreferenceStore,
  private val dispatchers: AppCoroutineDispatchers,
  private val api: ApiBase
) : QueueUseCase {
  private val genreRepository = repositories.genreRepository
  private val artistRepository = repositories.artistRepository
  private val albumRepository = repositories.albumRepository
  private val trackRepository = repositories.trackRepository

  override suspend fun queue(
    id: Long,
    meta: Meta,
    action: Queue
  ) = withContext(dispatchers.network) {
    val selectedAction = when (action) {
      Default -> settings.getDefaultAction()
      PlayAlbum,
      PlayArtist -> PlayAll
      else -> action
    }

    val (paths, path) = when (meta) {
      Genre -> QueueData(tracksForGenre(id))
      Artist -> QueueData(tracksForArtist(id))
      Album -> QueueData(tracksForAlbum(id))
      Track -> tracks(id, action)
    }

    val success = queueRequest(selectedAction, paths, path)
    return@withContext QueueResult(success, paths.size)
  }

  override suspend fun queuePath(path: String): QueueResult = Either.catch {
    queueRequest(Queue.Now, listOf(path), path)
  }.fold(
    {
      Timber.e(it)
      QueueResult(false, 1)
    },
    {
      QueueResult(it, 1)
    }
  )

  private suspend fun tracksForGenre(id: Long): List<String> =
    withContext(dispatchers.database) {
      val genre = genreRepository.getById(id)
      if (genre != null) {
        trackRepository.getTrackPaths(query = TrackQuery.Genre(genre.genre))
      } else {
        emptyList()
      }
    }

  private suspend fun tracksForArtist(id: Long): List<String> = withContext(dispatchers.database) {
    val artist = artistRepository.getById(id)
    if (artist != null) {
      trackRepository.getTrackPaths(query = TrackQuery.Artist(artist.artist))
    } else {
      emptyList()
    }
  }

  private suspend fun tracksForAlbum(id: Long): List<String> = withContext(dispatchers.database) {
    val album = albumRepository.getById(id)
    if (album != null) {
      trackRepository.getTrackPaths(TrackQuery.Album(album.album, album.artist))
    } else {
      emptyList()
    }
  }

  private suspend fun tracks(
    id: Long,
    action: Queue
  ): QueueData = withContext(dispatchers.database) {
    val track = trackRepository.getById(id)
      ?: throw IllegalArgumentException("$action is not supported")

    when (action) {
      PlayAll -> QueueData(trackRepository.getTrackPaths(TrackQuery.All), track.src)
      PlayAlbum -> QueueData(
        trackRepository.getTrackPaths(TrackQuery.Album(track.album, track.albumArtist)),
        track.src
      )
      PlayArtist -> QueueData(
        trackRepository.getTrackPaths(TrackQuery.Artist(track.artist)),
        track.src

      )
      else -> QueueData(listOf(track.src))
    }
  }

  private suspend fun queueRequest(
    type: Queue,
    tracks: List<String>,
    play: String? = null
  ): Boolean {
    return withContext(dispatchers.network) {
      Timber.v("Queueing ${tracks.size} $type")
      return@withContext Either.catch {
        val response = api.getItem(
          Protocol.NowPlayingQueue,
          QueueResponse::class,
          QueuePayload(type.action, tracks, play)
        )
        response.code == CoverPayload.SUCCESS
      }.fold({
        Timber.e(it)
        false
      }, { it })
    }
  }
}
