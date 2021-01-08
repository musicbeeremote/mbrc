package com.kelsos.mbrc.features.queue

import arrow.core.Either
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.Meta.Album
import com.kelsos.mbrc.common.Meta.Artist
import com.kelsos.mbrc.common.Meta.Genre
import com.kelsos.mbrc.common.Meta.Track
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue.Default
import com.kelsos.mbrc.features.queue.Queue.PlayAlbum
import com.kelsos.mbrc.features.queue.Queue.PlayAll
import com.kelsos.mbrc.features.queue.Queue.PlayArtist
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
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
    meta: Meta,
    action: Queue
  ): Either<Throwable, Int> {

    val selectedAction = when (action) {
      Default -> Queue.fromString(settings.defaultAction)
      PlayAlbum,
      PlayArtist -> PlayAll
      else -> action
    }

    return withContext(dispatchers.network) {
      val (paths, path) = when (meta) {
        Genre -> Pair(tracksForGenre(id), null)
        Artist -> Pair(tracksForArtist(id), null)
        Album -> Pair(tracksForAlbum(id), null)
        Track -> tracks(id, action)
      }

      queueApi.queue(selectedAction, paths, path).map { it.code }
    }
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
    action: Queue
  ): Pair<List<String>, String?> = withContext(dispatchers.database) {
    val track = trackRepository.getById(id)
    if (track != null) {
      when (action) {
        PlayAlbum -> Pair(
          trackRepository.getAlbumTrackPaths(
            track.album,
            track.albumArtist
          ),
          track.src
        )
        PlayArtist -> Pair(
          trackRepository.getArtistTrackPaths(track.artist),
          track.src
        )
        PlayAll -> Pair(trackRepository.getAllTrackPaths(), track.src)
        else -> Pair(listOf(track.src), null)
      }
    } else {
      Pair(emptyList(), null)
    }
  }
}
