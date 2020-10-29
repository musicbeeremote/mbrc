package com.kelsos.mbrc.helper

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.CoverPayload
import com.kelsos.mbrc.data.QueuePayload
import com.kelsos.mbrc.data.QueueResponse
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.repository.TrackRepository
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class QueueHandler
@Inject
constructor(
  private val settings: BasicSettingsHelper,
  private val trackRepository: TrackRepository,
  private val service: ApiBase,
  private val dispatchers: AppDispatchers
) {
  private suspend fun queue(
    @Queue.Action type: String,
    tracks: List<String>,
    play: String? = null
  ): Boolean {
    return withContext(dispatchers.io) {
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
    @Queue.Action type: String,
    album: String,
    artist: String
  ) {
    try {
      val paths = trackRepository.getAlbumTrackPaths(album, artist)
      queue(type, paths)
    } catch (e: Exception) {
      Timber.e(e)
    }
  }

  suspend fun queueArtist(
    @Queue.Action type: String,
    artist: String
  ) {
    try {
      val paths = trackRepository.getArtistTrackPaths(artist)
      queue(type, paths)
    } catch (e: Exception) {
      Timber.e(e)
    }
  }

  suspend fun queueGenre(
    @Queue.Action type: String,
    genre: String
  ) {
    try {
      val paths = trackRepository.getGenreTrackPaths(genre)
      queue(type, paths)
    } catch (e: Exception) {
      Timber.e(e)
    }
  }

  suspend fun queueTrack(track: Track, @Queue.Action type: String, queueAlbum: Boolean = false) {
    val trackSource: List<String>
    val path: String?
    trackSource = if (type == Queue.ADD_ALL) {
      path = track.src
      if (queueAlbum) {
        trackRepository.getAlbumTrackPaths(track.album!!, track.albumArtist!!)
      } else {
        trackRepository.getAllTrackPaths()
      }
    } else {
      path = null
      listOf(track.src!!)
    }

    queue(type, trackSource, path)
  }

  suspend fun queueTrack(track: Track, queueAlbum: Boolean = false) {
    queueTrack(track, settings.defaultAction, queueAlbum)
  }
}
