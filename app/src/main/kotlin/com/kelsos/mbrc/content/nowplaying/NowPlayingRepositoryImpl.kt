package com.kelsos.mbrc.content.nowplaying

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class NowPlayingRepositoryImpl(
  private val remoteDataSource: ApiBase,
  private val dao: NowPlayingDao,
  private val dispatchers: AppCoroutineDispatchers
) : NowPlayingRepository {

  private val mapper = NowPlayingDtoMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override suspend fun getAll(): DataSource.Factory<Int, NowPlayingEntity> {
    return withContext(dispatchers.database) { dao.getAll() }
  }

  override suspend fun getRemote() {
    val added = epoch()
    remoteDataSource.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class).blockingForEach {
      launch(CommonPool) {
        val list = it.map { mapper.map(it).apply { dateAdded = added } }
        withContext(dispatchers.database) {
          dao.insertAll(list)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, NowPlayingEntity> {
    return dao.search(term)
  }

  override suspend fun cacheIsEmpty(): Boolean = withContext(dispatchers.database) { dao.count() == 0L }

  override fun move(from: Int, to: Int) {
    TODO("implement move")
  }
}