package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.data.ArtistDao
import com.kelsos.mbrc.features.library.data.ArtistEntityMapper
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.dto.ArtistDto
import com.kelsos.mbrc.features.library.dto.ArtistDtoMapper
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ArtistRepositoryImpl(
  private val dao: ArtistDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : ArtistRepository {

  private val mapper = ArtistDtoMapper()
  private val entity2model =
    ArtistEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getArtistByGenre(genre: String): DataSource.Factory<Int, Artist> {
    return dao.getArtistByGenre(genre).map { entity2model.map(it) }
  }

  override fun getAll(): DataSource.Factory<Int, Artist> {
    return dao.getAll().map { entity2model.map(it) }
  }

  override fun allArtists(): DataModel<Artist> {
    return DataModel(dao.getAll().map {
      entity2model.map(
        it
      )
    }, dao.getAllIndexes())
  }

  override fun albumArtists(): DataModel<Artist> {
    return DataModel(
      factory = dao.getAlbumArtists().map { entity2model.map(it) },
      indexes = dao.getAlbumArtistIndexes()
    )
  }

  override suspend fun getRemote(): Try<Unit> = Try {
    val added = epoch()
    val data = remoteDataSource.getAllPages(Protocol.LibraryBrowseArtists, ArtistDto::class)
    data.blockingForEach { artists ->
      runBlocking(dispatchers.disk) {
        val items = artists.map { mapper.map(it).apply { dateAdded = added } }
        withContext(dispatchers.database) {
          dao.insertAll(items)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, Artist> {
    return dao.search(term).map { entity2model.map(it) }
  }

  override fun getAlbumArtistsOnly(): DataSource.Factory<Int, Artist> {
    return dao.getAlbumArtists().map { entity2model.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean {
    return withContext(dispatchers.database) { dao.count() == 0L }
  }
}