package com.kelsos.mbrc.content.nowplaying

import androidx.paging.DataSource
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NowPlayingRepositoryImpl
@Inject constructor(
  private val dao: NowPlayingDao,
  private val remoteDataSource: RemoteNowPlayingDataSource,
  private val dispatchers: AppDispatchers
) : NowPlayingRepository {
  private val mapper = NowPlayingDtoMapper()

  override suspend fun getAll(): DataSource.Factory<Int, NowPlaying> = dao.getAll().map { it }

  override suspend fun getAndSaveRemote(): DataSource.Factory<Int, NowPlaying> {
    getRemote()
    return dao.getAll().map { it }
  }

  override suspend fun getRemote() {
    val added = epoch()
    withContext(dispatchers.io) {
      remoteDataSource.fetch().onCompletion {
        dao.removePreviousEntries(added)
      }.collect { item ->
        val list = item.map { mapper.map(it).apply { dateAdded = added } }
        dao.insertAll(list)
      }
    }
  }

  override suspend fun search(term: String): DataSource.Factory<Int, NowPlaying> =
    dao.search(term).map { it }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
