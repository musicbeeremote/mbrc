package com.kelsos.mbrc.features.nowplaying.repository

import androidx.paging.DataSource
import arrow.core.Either
import com.kelsos.mbrc.common.data.Progress
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.nowplaying.NowPlayingDtoMapper
import com.kelsos.mbrc.features.nowplaying.NowPlayingEntityMapper
import com.kelsos.mbrc.features.nowplaying.data.CachedNowPlaying
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingDao
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingEntity
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import timber.log.Timber

interface NowPlayingRepository :
  Repository<NowPlaying> {

  suspend fun move(from: Int, to: Int)
  suspend fun remove(position: Int)
  suspend fun findPosition(query: String): Int
}

class NowPlayingRepositoryImpl(
  private val remoteDataSource: ApiBase,
  private val dao: NowPlayingDao,
  private val dispatchers: AppCoroutineDispatchers
) : NowPlayingRepository {

  private fun NowPlayingEntity.key(): String {
    return "$path-$position"
  }

  private fun CachedNowPlaying.key(): String {
    return "$path-$position"
  }

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, NowPlaying> {
    return dao.getAll().map { NowPlayingEntityMapper.map(it) }
  }

  override suspend fun getRemote(progress: Progress): Either<Throwable, Unit> = Either.catch {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(
      Protocol.NowPlayingList,
      NowPlayingDto::class,
      progress
    )
    val cached = dao.cached().associateBy { it.key() }
    pages.collect { nowPlaying ->
      withContext(dispatchers.io) {
        val list = nowPlaying.map { NowPlayingDtoMapper.map(it).apply { dateAdded = added } }

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

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, NowPlaying> {
    return dao.search(term).map { NowPlayingEntityMapper.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean = withContext(dispatchers.database) {
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
      return@withContext NowPlayingEntityMapper.map(entity)
    }
  }
}