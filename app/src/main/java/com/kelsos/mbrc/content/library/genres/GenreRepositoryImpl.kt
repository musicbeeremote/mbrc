package com.kelsos.mbrc.content.library.genres

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GenreRepositoryImpl
constructor(
  private val remoteDataSource: ApiBase,
  private val dao: GenreDao,
  private val dispatchers: AppCoroutineDispatchers
) : GenreRepository {

  private val mapper = GenreDtoMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, GenreEntity> {
    return dao.getAll()
  }

  override suspend fun getRemote() {
    val added = epoch()

    remoteDataSource.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class).blockingForEach { genres ->
      runBlocking(dispatchers.disk) {

        val items = genres.map { mapper.map(it).apply { dateAdded = added } }

        withContext(dispatchers.database) {
          dao.insertAll(items)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, GenreEntity> {
    return dao.search(term)
  }

  override suspend fun cacheIsEmpty(): Boolean = withContext(dispatchers.database) {
    dao.count() == 0L
  }

  override suspend fun allGenres(): DataModel<GenreEntity> {
    return withContext(dispatchers.database) { DataModel(dao.getAll(), dao.getAllIndexes()) }
  }
}