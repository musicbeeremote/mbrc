package com.kelsos.mbrc.features.nowplaying.repository

import androidx.paging.PagingData
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.nowplaying.data.CachedNowPlaying
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingDao
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingEntity
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.toEntity
import com.kelsos.mbrc.features.nowplaying.toNowPlaying
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import timber.log.Timber

interface NowPlayingRepository : Repository<NowPlaying> {
  suspend fun move(from: Int, to: Int)
  suspend fun remove(position: Int)
  suspend fun findPosition(query: String): Int
}

class NowPlayingRepositoryImpl(
  private val api: ApiBase,
  private val dao: NowPlayingDao,
  private val dispatchers: AppCoroutineDispatchers
) : NowPlayingRepository {

  private fun NowPlayingEntity.key(): String {
    return "$path-$position"
  }

  private fun CachedNowPlaying.key(): String {
    return "$path-$position"
  }

  override suspend fun count(): Long = withContext(dispatchers.database) { dao.count() }

  override fun getAll(): Flow<PagingData<NowPlaying>> = paged({ dao.getAll() }) {
    it.toNowPlaying()
  }

  override fun all(): List<NowPlaying> = dao.all().map { it.toNowPlaying() }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    withContext(dispatchers.network) {
      val added = epoch()
      val cached = withContext(dispatchers.database) {
        dao.cached().associateBy { it.key() }
      }
      api.getAllPages(
        Protocol.NowPlayingList,
        NowPlayingDto::class,
        progress
      ).onCompletion {
        withContext(dispatchers.database) {
          dao.removePreviousEntries(added)
        }
      }.collect { item ->
        val list = item.map { it.toEntity().apply { dateAdded = added } }

        val existing = list.filter { cached.containsKey(it.key()) }
        val new = list.minus(existing)
        for (entity in existing) {
          entity.id = checkNotNull(cached[entity.key()]).id
        }
        withContext(dispatchers.database) {
          Timber.v("updating ${existing.size} and inserting ${new.size} items")
          dao.update(existing)
          dao.insertAll(new)
        }
      }
    }
  }

  override fun search(
    term: String
  ): Flow<PagingData<NowPlaying>> = paged({ dao.search(term) }) { it.toNowPlaying() }

  override fun simpleSearch(term: String): List<NowPlaying> {
    return dao.simpleSearch(term).map { it.toNowPlaying() }
  }

  override suspend fun cacheIsEmpty(): Boolean =
    withContext(dispatchers.database) {
      dao.count() == 0L
    }

  override suspend fun move(from: Int, to: Int) = withContext(dispatchers.database) {
    dao.move(from, to)
  }

  override suspend fun remove(position: Int) = withContext(dispatchers.database) {
    dao.remove(position)
  }

  override suspend fun findPosition(query: String): Int = withContext(dispatchers.database) {
    return@withContext dao.findPositionByQuery(query) ?: -1
  }

  override suspend fun getById(id: Long): NowPlaying? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext entity.toNowPlaying()
    }
  }
}
