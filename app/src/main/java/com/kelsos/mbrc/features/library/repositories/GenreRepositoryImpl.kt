package com.kelsos.mbrc.features.library.repositories

import androidx.paging.PagingData
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.data.GenreDao
import com.kelsos.mbrc.features.library.data.toGenre
import com.kelsos.mbrc.features.library.dto.GenreDto
import com.kelsos.mbrc.features.library.dto.toEntity
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class GenreRepositoryImpl(
  private val api: ApiBase,
  private val dao: GenreDao,
  private val dispatchers: AppCoroutineDispatchers
) : GenreRepository {

  override suspend fun count(): Long = withContext(dispatchers.database) { dao.count() }

  override fun getAll(): Flow<PagingData<Genre>> = paged({ dao.getAll() }) {
    it.toGenre()
  }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    withContext(dispatchers.network) {
      val added = epoch()
      val stored = withContext(dispatchers.database) {
        dao.genres().associate { it.genre to it.id }
      }
      val allPages = api.getAllPages(
        Protocol.LibraryBrowseGenres,
        GenreDto::class,
        progress
      )

      allPages.onCompletion {
        withContext(dispatchers.database) {
          dao.removePreviousEntries(added)
        }
      }.collect { genres ->
        val items = genres.map {
          it.toEntity().apply {
            dateAdded = added

            val id = stored[it.genre]
            if (id != null) {
              this.id = id
            }
          }
        }
        withContext(dispatchers.database) {
          dao.insertAll(items.filter { it.id <= 0 })
          dao.update(items.filter { it.id > 0 })
        }
      }
    }
  }

  override fun search(term: String): Flow<PagingData<Genre>> = paged({ dao.search(term) }) {
    it.toGenre()
  }

  override suspend fun cacheIsEmpty(): Boolean =
    withContext(dispatchers.database) { dao.count() == 0L }

  override suspend fun getById(id: Long): Genre? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext entity.toGenre()
    }
  }
}
