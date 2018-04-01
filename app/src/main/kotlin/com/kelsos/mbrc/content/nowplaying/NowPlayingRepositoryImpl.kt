package com.kelsos.mbrc.content.nowplaying

import androidx.paging.PagingData
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.epoch
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NowPlayingRepositoryImpl
@Inject constructor(
  private val dao: NowPlayingDao,
  private val api: ApiBase,
  private val dispatchers: AppDispatchers
) : NowPlayingRepository {
  private val mapper = NowPlayingDtoMapper()

  override suspend fun getAll(): Flow<PagingData<NowPlaying>> = dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<NowPlaying>> {
    getRemote()
    return dao.getAll().paged()
  }

  override suspend fun getRemote() {
    val added = epoch()
    withContext(dispatchers.io) {
      api.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class)
        .onCompletion {
          dao.removePreviousEntries(added)
        }
        .collect { item ->
          val list = item.map { mapper.map(it).apply { dateAdded = added } }
          dao.insertAll(list)
        }
    }
  }

  override suspend fun search(term: String): Flow<PagingData<NowPlaying>> =
    dao.search(term).paged()

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()

  override fun move(from: Int, to: Int) {
    TODO("implement move")
  }
}
