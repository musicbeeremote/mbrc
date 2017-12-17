package com.kelsos.mbrc.content.library.genres

import androidx.paging.DataSource
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenreRepositoryImpl
@Inject
constructor(
  private val dao: GenreDao,
  private val remoteDataSource: RemoteGenreDataSource,
  private val dispatchers: AppDispatchers
) : GenreRepository {

  private val mapper = GenreDtoMapper()

  override suspend fun getAll(): DataSource.Factory<Int, Genre> = dao.getAll().map { it }

  override suspend fun getAndSaveRemote(): DataSource.Factory<Int, Genre> {
    getRemote()
    return dao.getAll().map { it }
  }

  override suspend fun getRemote() {
    withContext(dispatchers.io) {
      val added = epoch()
      remoteDataSource.fetch()
        .onCompletion {
          dao.removePreviousEntries(added)
        }
        .collect { genres ->
          val data = genres.map { mapper.map(it).apply { dateAdded = added } }
          dao.insertAll(data)
        }
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, Genre> =
    dao.search(term).map { it }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
