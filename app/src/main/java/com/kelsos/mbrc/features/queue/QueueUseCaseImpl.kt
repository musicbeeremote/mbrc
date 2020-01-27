package com.kelsos.mbrc.features.queue

import com.kelsos.mbrc.common.Meta.Type
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue.Action
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import kotlinx.coroutines.withContext

class QueueUseCaseImpl(
  private val trackRepository: TrackRepository,
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val settings: DefaultActionPreferenceStore,
  private val dispatchers: AppCoroutineDispatchers,
  private val queueHandler: QueueHandler
) : QueueUseCase {
  override suspend fun queue(
    id: Int,
    @Type meta: Int,
    @Action action: String
  ) = withContext(dispatchers.disk) {}

  private suspend fun tracksForGenre(id: Int): List<String> =
    withContext(dispatchers.database) {
      val genre = genreRepository.getById(id)
      if (genre != null) {
        trackRepository.getGenreTrackPaths(genre = genre.genre)
      } else {
        emptyList()
      }
    }

  private suspend fun tracksForArtist(id: Int): List<String> = withContext(dispatchers.database) {
    val artist = artistRepository.getById(id)
    if (artist != null) {
      trackRepository.getArtistTrackPaths(artist = artist.artist)
    } else {
      emptyList()
    }
  }

  private suspend fun tracksForAlbum(id: Int): List<String> = withContext(dispatchers.database) {
    val album = albumRepository.getById(id)
    if (album != null) {
      trackRepository.getAlbumTrackPaths(album.album, album.artist)
    } else {
      emptyList()
    }
  }

  private suspend fun tracks(
    id: Int,
    @Action action: String
  ): Pair<List<String>, String?> = withContext(dispatchers.database) {
    val track = trackRepository.getById(id)
    if (track != null) {
      when (action) {
        Queue.ADD_ALBUM -> Pair(
          trackRepository.getAlbumTrackPaths(
            track.album,
            track.albumArtist
          ),
          track.src
        )
        Queue.ADD_ALL -> Pair(trackRepository.getAllTrackPaths(), track.src)
        else -> Pair(listOf(track.src), null)
      }
    } else {
      Pair(emptyList(), null)
    }
  }
}
