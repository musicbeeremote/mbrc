package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.data.AlbumDao
import com.kelsos.mbrc.features.library.data.AlbumEntityMapper
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.dto.AlbumDto
import com.kelsos.mbrc.features.library.dto.AlbumDtoMapper
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AlbumRepositoryImpl(
  private val dao: AlbumDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : AlbumRepository {

  private val mapper = AlbumDtoMapper()
  private val view2model =
    AlbumEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, Album> {
    return dao.getAlbumsByArtist(artist).map { view2model.map(it) }
  }

  override fun getAll(): DataSource.Factory<Int, Album> {
    return dao.getAll().map { view2model.map(it) }
  }

  override suspend fun getRemote(): Try<Unit> = Try {
    val added = epoch()
    val data = remoteDataSource.getAllPages(Protocol.LibraryBrowseAlbums, AlbumDto::class)
    data.blockingForEach { albums ->
      runBlocking(dispatchers.disk) {
        val list = albums.map { mapper.map(it).apply { dateAdded = added } }
        withContext(dispatchers.database) {
          dao.insert(list)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, Album> {
    return dao.search(term).map { view2model.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override fun getAlbumsSorted(): DataModel<Album> {
    return DataModel(
      factory = dao.getAll().map { view2model.map(it) },
      indexes = dao.getIndexes()
    )
  }
}