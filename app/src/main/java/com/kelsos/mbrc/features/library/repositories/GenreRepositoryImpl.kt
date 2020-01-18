package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import arrow.core.Try
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.data.GenreDao
import com.kelsos.mbrc.features.library.data.GenreEntityMapper
import com.kelsos.mbrc.features.library.dto.GenreDto
import com.kelsos.mbrc.features.library.dto.GenreDtoMapper
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GenreRepositoryImpl(
  private val remoteDataSource: ApiBase,
  private val dao: GenreDao,
  private val dispatchers: AppCoroutineDispatchers
) : GenreRepository {

  private val mapper = GenreDtoMapper()
  private val dao2Model =
    GenreEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, Genre> {
    return dao.getAll().map { dao2Model.map(it) }
  }

  override suspend fun getRemote(): Try<Unit> = Try {
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
    return DataModel(dao.getAll().map {
      dao2Model.map(
        it
      )
    }, dao.getAllIndexes())
  }
}