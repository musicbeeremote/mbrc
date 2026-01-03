package com.kelsos.mbrc.feature.library.queue

import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.common.utilities.asFailure
import com.kelsos.mbrc.core.common.utilities.asSuccess
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.data.library.track.TrackQuery
import com.kelsos.mbrc.core.data.library.track.TrackRepository
import com.kelsos.mbrc.core.networking.api.QueueApi
import com.kelsos.mbrc.core.networking.dto.QueuePayload
import com.kelsos.mbrc.core.networking.protocol.payloads.CoverPayload
import com.kelsos.mbrc.core.queue.PathQueueUseCase
import com.kelsos.mbrc.core.queue.Queue
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class QueueHandler(
  private val settings: LibrarySettings,
  private val trackRepository: TrackRepository,
  private val queueApi: QueueApi,
  private val dispatchers: AppCoroutineDispatchers
) : PathQueueUseCase {
  private suspend fun queue(type: Queue, tracks: List<String>, play: String? = null): Boolean {
    return withContext(dispatchers.io) {
      Timber.v("Queueing ${tracks.size} $type")
      try {
        val response = queueApi.queue(QueuePayload(type.action, tracks, play))
        return@withContext response.code == CoverPayload.SUCCESS
      } catch (e: IOException) {
        Timber.e(e)
        return@withContext false
      }
    }
  }

  suspend fun queueAlbum(type: Queue, album: String, artist: String): Outcome<Int> = try {
    val paths = withContext(dispatchers.database) {
      trackRepository.getTrackPaths(TrackQuery.Album(album, artist))
    }
    if (queue(type, paths)) {
      paths.size.asSuccess()
    } else {
      AppError.OperationFailed.asFailure()
    }
  } catch (e: IOException) {
    Timber.e(e)
    AppError.NetworkUnavailable.asFailure()
  }

  suspend fun queueArtist(type: Queue, artist: String): Outcome<Int> = try {
    val paths = withContext(dispatchers.database) {
      trackRepository.getTrackPaths(TrackQuery.Artist(artist))
    }
    if (queue(type, paths)) {
      paths.size.asSuccess()
    } else {
      AppError.OperationFailed.asFailure()
    }
  } catch (e: IOException) {
    Timber.e(e)
    AppError.NetworkUnavailable.asFailure()
  }

  suspend fun queueGenre(type: Queue, genre: String): Outcome<Int> = try {
    val paths = withContext(dispatchers.database) {
      trackRepository.getTrackPaths(TrackQuery.Genre(genre))
    }
    if (queue(type, paths)) {
      paths.size.asSuccess()
    } else {
      AppError.OperationFailed.asFailure()
    }
  } catch (e: IOException) {
    Timber.e(e)
    AppError.NetworkUnavailable.asFailure()
  }

  override suspend fun queuePath(path: String): Outcome<Int> = try {
    if (queue(Queue.Now, listOf(path))) {
      1.asSuccess()
    } else {
      AppError.OperationFailed.asFailure()
    }
  } catch (e: IOException) {
    Timber.e(e)
    AppError.NetworkUnavailable.asFailure()
  }

  suspend fun queueTrack(track: Track, type: Queue, queueAlbum: Boolean = false): Outcome<Int> {
    val trackSource: List<String>
    val path: String?
    var action = type
    trackSource = withContext(dispatchers.database) {
      when (type) {
        Queue.AddAll -> {
          path = track.src
          if (queueAlbum) {
            trackRepository.getTrackPaths(TrackQuery.Album(track.album, track.albumArtist))
          } else {
            trackRepository.getTrackPaths(TrackQuery.All)
          }
        }

        Queue.PlayAlbum -> {
          action = Queue.AddAll
          path = track.src
          trackRepository.getTrackPaths(TrackQuery.Album(track.album, track.albumArtist))
        }

        Queue.PlayArtist -> {
          action = Queue.AddAll
          path = track.src
          trackRepository.getTrackPaths(TrackQuery.Artist(track.artist))
        }

        else -> {
          path = null
          listOf(track.src)
        }
      }
    }

    return if (queue(action, trackSource, path)) {
      trackSource.size.asSuccess()
    } else {
      AppError.OperationFailed.asFailure()
    }
  }

  suspend fun queueTrack(track: Track, queueAlbum: Boolean = false): Outcome<Int> = queueTrack(
    track,
    Queue.fromTrackAction(settings.libraryTrackDefaultActionFlow.first()),
    queueAlbum
  )
}
