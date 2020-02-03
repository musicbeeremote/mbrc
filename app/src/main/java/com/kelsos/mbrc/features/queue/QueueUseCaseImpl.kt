package com.kelsos.mbrc.features.queue

import com.kelsos.mbrc.common.Meta.ALBUM
import com.kelsos.mbrc.common.Meta.ARTIST
import com.kelsos.mbrc.common.Meta.GENRE
import com.kelsos.mbrc.common.Meta.TRACK
import com.kelsos.mbrc.common.Meta.Type
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue.Action
import com.kelsos.mbrc.features.queue.Queue.DEFAULT
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class QueueUseCaseImpl(
  private val trackRepository: TrackRepository,
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val settings: DefaultActionPreferenceStore,
  private val dispatchers: AppCoroutineDispatchers,
  private val queueApi: QueueApi
) : QueueUseCase {
  override suspend fun queue(
    id: Long,
    @Type meta: Int,
    @Action action: String
  ) = withContext(dispatchers.disk) {
    val selectedAction = if (action == DEFAULT) settings.defaultAction else action
    val response = withContext(dispatchers.network) {
      val (paths, path) = when (meta) {
        GENRE -> Pair(tracksForGenre(id), null)
        ARTIST -> Pair(tracksForArtist(id), null)
        ALBUM -> Pair(tracksForAlbum(id), null)
        TRACK -> tracks(id, action)
        else -> error("Invalid value $meta")
      }

      queueApi.queue(selectedAction, paths, path).await()
    }
    response.code
  }

  private suspend fun tracksForGenre(id: Long): List<String> =
    withContext(dispatchers.database) {
      val genre = genreRepository.getById(id)
      if (genre != null) {
        trackRepository.getGenreTrackPaths(genre = genre.genre)
      } else {
        emptyList()
      }
    }

  private suspend fun tracksForArtist(id: Long): List<String> = withContext(dispatchers.database) {
    val artist = artistRepository.getById(id)
    if (artist != null) {
      trackRepository.getArtistTrackPaths(artist = artist.artist)
    } else {
      emptyList()
    }
  }

  private suspend fun tracksForAlbum(id: Long): List<String> = withContext(dispatchers.database) {
    val album = albumRepository.getById(id)
    if (album != null) {
      trackRepository.getAlbumTrackPaths(album.album, album.artist)
    } else {
      emptyList()
    }
  }

  private suspend fun tracks(
    id: Long,
    @Action action: String
  ): Pair<List<String>, String?> = withContext(dispatchers.database) {
    val track = trackRepository.getById(id)
    if (track != null) {
      when (action) {
        Queue.ADD_ALBUM -> Pair(trackRepository.getAlbumTrackPaths(
          track.album,
          track.albumArtist
        ), track.src)
        Queue.ADD_ALL -> Pair(trackRepository.getAllTrackPaths(), track.src)
        else -> Pair(listOf(track.src), null)
      }
    } else {
      Pair(emptyList(), null)
    }
  }
}