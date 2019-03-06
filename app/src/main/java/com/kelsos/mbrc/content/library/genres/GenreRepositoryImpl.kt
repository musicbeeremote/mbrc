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
  private val dao2Model = GenreEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, Genre> {
    return dao.getAll().map { dao2Model.map(it) }
  }

  override suspend fun getRemote() {
    val added = epoch()
    val stored = dao.genres().associate { it.genre to it.id }
    val data = remoteDataSource.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class)
    data.blockingForEach { genres ->
      runBlocking(dispatchers.disk) {

        val items = genres.map {
          mapper.map(it).apply {
            dateAdded = added

            val id = stored[it.genre]
            if (id != null) {
              this.id = id
            }
          }
        }

        withContext(dispatchers.database) {
          dao.insertAll(items)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, Genre> {
    return dao.search(term).map { dao2Model.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean = withContext(dispatchers.database) {
    dao.count() == 0L
  }

  override fun allGenres(): DataModel<Genre> {
    return DataModel(dao.getAll().map { dao2Model.map(it) }, dao.getAllIndexes())
  }
}