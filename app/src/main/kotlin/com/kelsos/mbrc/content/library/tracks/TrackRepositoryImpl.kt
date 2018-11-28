package com.kelsos.mbrc.content.library.tracks

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
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

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, TrackEntity> {
    return dao.getAll()
  }

  override fun getAlbumTracks(
    album: String,
    artist: String
  ): DataSource.Factory<Int, TrackEntity> {
    return dao.getAlbumTracks(album, artist)
  }

  override fun allTracks(): DataModel<TrackEntity> {
    return DataModel(dao.getAll(), dao.getAllIndexes())
  }

  override fun getNonAlbumTracks(artist: String): DataSource.Factory<Int, TrackEntity> {
    return dao.getNonAlbumTracks(artist)
  }

  override suspend fun getRemote() {
    val added = epoch()

    remoteDataSource.getAllPages(Protocol.LibraryBrowseTracks, TrackDto::class).blockingForEach { tracks ->
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

  override fun search(term: String): DataSource.Factory<Int, TrackEntity> {
    return dao.search(term)
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