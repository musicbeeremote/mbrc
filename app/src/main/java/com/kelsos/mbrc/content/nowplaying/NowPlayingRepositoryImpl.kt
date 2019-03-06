package com.kelsos.mbrc.content.nowplaying

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NowPlayingRepositoryImpl(
  private val remoteDataSource: ApiBase,
  private val dao: NowPlayingDao,
  private val dispatchers: AppCoroutineDispatchers
) : NowPlayingRepository {

  private val mapper = NowPlayingDtoMapper()
  private val entity2model = NowPlayingEntityMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, NowPlaying> {
    return dao.getAll().map { entity2model.map(it) }
  }

  override suspend fun getRemote() {
    val added = epoch()
    val pages = remoteDataSource.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class)
    pages.blockingForEach { nowPlaying ->
      runBlocking(dispatchers.disk) {
        val list = nowPlaying.map { mapper.map(it).apply { dateAdded = added } }
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
    return dao.search(term).map { entity2model.map(it) }
  }

  override suspend fun cacheIsEmpty(): Boolean = withContext(dispatchers.database) {
    dao.count() == 0L
  }

  override fun move(from: Int, to: Int) {
    TODO("implement move")
  }
}