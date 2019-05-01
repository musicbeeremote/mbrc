package com.kelsos.mbrc.features.nowplaying.repository

import androidx.paging.DataSource
import arrow.core.Try
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.nowplaying.NowPlayingDtoMapper
import com.kelsos.mbrc.features.nowplaying.NowPlayingEntityMapper
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingDao
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.interfaces.data.Repository
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

interface NowPlayingRepository : Repository<NowPlaying> {

  suspend fun move(from: Int, to: Int)
  suspend fun remove(position: Int)
  suspend fun findPosition(query: String): Int
}

class NowPlayingRepositoryImpl(
  private val remoteDataSource: ApiBase,
  private val dao: NowPlayingDao,
  private val dispatchers: AppCoroutineDispatchers
) : NowPlayingRepository {

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, NowPlaying> {
    return dao.getAll().map { NowPlayingEntityMapper.map(it) }
  }

  override suspend fun getRemote(): Try<Unit> = Try {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(
      Protocol.NowPlayingList,
      NowPlayingDto::class
    )
    pages.blockingForEach { nowPlaying ->
      runBlocking(dispatchers.disk) {
        val list = nowPlaying.map { NowPlayingDtoMapper.map(it).apply { dateAdded = added } }
        withContext(dispatchers.database) {
          dao.insertAll(list)
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
}