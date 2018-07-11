package com.kelsos.mbrc.content.library.artists

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext


class ArtistRepositoryImpl(
  private val dao: ArtistDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : ArtistRepository {

  private val mapper = ArtistDtoMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override suspend fun getArtistByGenre(genre: String): DataSource.Factory<Int, ArtistEntity> {
    return dao.getArtistByGenre(genre)
  }

  override suspend fun getAll(): DataSource.Factory<Int, ArtistEntity> {
    return dao.getAll()
  }

  override suspend fun allArtists(): DataModel<ArtistEntity> {
    return DataModel(dao.getAll(), dao.getAllIndexes())
  }

  override suspend fun albumArtists(): DataModel<ArtistEntity> {
    return DataModel(dao.getAlbumArtists(), dao.getAlbumArtistIndexes())
  }

  override suspend fun getRemote() {
    val added = epoch()
    remoteDataSource.getAllPages(Protocol.LibraryBrowseArtists, ArtistDto::class).blockingForEach {
      launch(dispatchers.disk) {
        val items = it.map { mapper.map(it).apply { dateAdded = added } }
        withContext(dispatchers.database) {
          dao.insertAll(items)
        }
      }
    }

    launch(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, ArtistEntity> {
    return dao.search(term)
  }

  override suspend fun getAlbumArtistsOnly(): DataSource.Factory<Int, ArtistEntity> {
    return dao.getAlbumArtists()
  }

  override suspend fun cacheIsEmpty(): Boolean {
    return withContext(dispatchers.database) { dao.count() == 0L }
  }
}