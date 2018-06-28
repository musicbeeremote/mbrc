package com.kelsos.mbrc.content.library.genres

import androidx.paging.PagingData
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class GenreRepositoryImpl(
  private val dao: GenreDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : GenreRepository {

  private val mapper = GenreDtoMapper()

  override suspend fun getAll(): Flow<PagingData<Genre>> = dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<Genre>> {
    getRemote()
    return dao.getAll().paged()
  }

  override suspend fun getRemote() {
    withContext(dispatchers.network) {
      val added = epoch()
      api.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class)
        .onCompletion {
          dao.removePreviousEntries(added)
        }
        .collect { genres ->
          val data = genres.map { mapper.map(it).apply { dateAdded = added } }
          dao.insertAll(data)
        }
    }
  }

  override suspend fun search(term: String): Flow<PagingData<Genre>> =
    dao.search(term).paged()

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
