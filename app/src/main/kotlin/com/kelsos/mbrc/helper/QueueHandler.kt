package com.kelsos.mbrc.helper

import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.cover.CoverPayload
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.QueuePayload
import com.kelsos.mbrc.content.nowplaying.queue.QueueResponse
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class QueueHandler
@Inject
constructor(
  private val settings: DefaultActionPreferenceStore,
  private val trackRepository: TrackRepository,
  private val service: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) {
  private suspend fun queue(
    @LibraryPopup.Action type: String,
    tracks: List<String>,
    play: String? = null
  ): Boolean {
    return withContext(dispatchers.network) {
      Timber.v("Queueing ${tracks.size} $type")
      try {
        val response = service.getItem(
          Protocol.NowPlayingQueue,
          QueueResponse::class,
          QueuePayload(type, tracks, play)
        )

        return@withContext response.code == CoverPayload.SUCCESS
      } catch (e: Exception) {
        Timber.e(e)
        return@withContext false
      }
    }
  }

  suspend fun queueAlbum(
    @LibraryPopup.Action type: String,
    album: String,
    artist: String
  ): QueueResult {
    var tracks = 0
    var success = false
    try {
      val paths = trackRepository.getAlbumTrackPaths(album, artist)
      tracks = paths.size
      success = queue(type, paths)
    } catch (e: Exception) {
      Timber.e(e)
    }
    return QueueResult(success, tracks)
  }

  suspend fun queueArtist(
    @LibraryPopup.Action type: String,
    artist: String
  ): QueueResult {
    var tracks = 0
    var success = false
    try {
      val paths = trackRepository.getArtistTrackPaths(artist)
      tracks = paths.size
      success = queue(type, paths)
    } catch (e: Exception) {
      Timber.e(e)
    }
    return QueueResult(success, tracks)
  }

  suspend fun queueGenre(
    @LibraryPopup.Action type: String,
    genre: String
  ): QueueResult {
    var tracks = 0
    var success = false
    try {
      val paths = trackRepository.getGenreTrackPaths(genre)
      tracks = paths.size
      success = queue(type, paths)
    } catch (e: Exception) {
      Timber.e(e)
    }
    return QueueResult(success, tracks)
  }

  suspend fun queuePath(path: String): QueueResult {
    var success = false
    try {
      success = queue(LibraryPopup.NOW, listOf(path))
    } catch (e: Exception) {
      Timber.e(e)
    }
    return QueueResult(success, 1)
  }

  suspend fun queueTrack(
    track: Track,
    @LibraryPopup.Action type: String,
    queueAlbum: Boolean = false
  ): QueueResult {
    val trackSource: List<String>
    val path: String?
    val success: Boolean
    val tracks: Int
    var action = type
    trackSource = when (type) {
      LibraryPopup.ADD_ALL -> {
        path = track.src
        if (queueAlbum) {
          trackRepository.getAlbumTrackPaths(track.album, track.albumArtist)
        } else {
          trackRepository.getAllTrackPaths()
        }
      }
      LibraryPopup.PLAY_ALBUM -> {
        action = LibraryPopup.ADD_ALL
        path = track.src
        trackRepository.getAlbumTrackPaths(track.album, track.albumArtist)
      }
      LibraryPopup.PLAY_ARTIST -> {
        action = LibraryPopup.ADD_ALL
        path = track.src
        trackRepository.getArtistTrackPaths(track.artist)
      }
      else -> {
        path = null
        listOf(track.src)
      }
    }

    tracks = trackSource.size
    success = queue(action, trackSource, path)
    return QueueResult(success, tracks)
  }

  suspend fun queueTrack(track: Track, queueAlbum: Boolean = false): QueueResult {
    return queueTrack(track, settings.defaultAction, queueAlbum)
  }
}
