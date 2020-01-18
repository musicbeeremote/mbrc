package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import arrow.core.Try
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.data.TrackDao
import com.kelsos.mbrc.features.library.dto.TrackDto
import com.kelsos.mbrc.features.library.dto.TrackDtoMapper
import com.kelsos.mbrc.features.library.data.TrackEntityMapper
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
  private val dao: TrackDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : TrackRepository {

  private val mapper = TrackDtoMapper()
  private val entity2model =
    TrackEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, Track> {
    return dao.getAll().map { entity2model.map(it) }
  }

  override fun getAlbumTracks(
    album: String,
    artist: String
  ): DataSource.Factory<Int, Track> {
    return dao.getAlbumTracks(album, artist).map { entity2model.map(it) }
  }

  override fun allTracks(): DataModel<Track> {
    return DataModel(dao.getAll().map {
      entity2model.map(
        it
      )
    }, dao.getAllIndexes())
  }

  override fun getNonAlbumTracks(artist: String): DataSource.Factory<Int, Track> {
    return dao.getNonAlbumTracks(artist).map { entity2model.map(it) }
  }

  override suspend fun getRemote(): Try<Unit> = Try {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(Protocol.LibraryBrowseTracks, TrackDto::class)
    pages.blockingForEach { tracks ->
      runBlocking(dispatchers.disk) {
        val trackData = tracks.map { mapper.map(it).apply { dateAdded = added } }
        val sources = trackData.map { it.src }

        withContext(dispatchers.database) {

          val matches = sources.chunked(50)
            .flatMap { dao.findMatchingIds(it) }
            .map { it.src to it.id }
            .toMap()

          val toUpdate = trackData.filter { matches.containsKey(it.src) }
          val toInsert = trackData.minus(toUpdate)

          dao.update(toUpdate.map { it.id = matches.getValue(it.src); it })
          dao.insertAll(toInsert)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, Track> {
    return dao.search(term).map { entity2model.map(it) }
  }

  override fun getGenreTrackPaths(genre: String): List<String> {
    return dao.getGenreTrackPaths(genre)
  }

  override fun getArtistTrackPaths(artist: String): List<String> {
    return dao.getArtistTrackPaths(artist)
  }

  override fun getAlbumTrackPaths(album: String, artist: String): List<String> {
    return dao.getAlbumTrackPaths(album, artist)
  }

  override fun getAllTrackPaths(): List<String> {
    return dao.getAllTrackPaths()
  }

  override suspend fun cacheIsEmpty(): Boolean {
    return dao.count() == 0L
  }
}