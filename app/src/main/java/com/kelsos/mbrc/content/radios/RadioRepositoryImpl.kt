package com.kelsos.mbrc.content.radios

import androidx.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RadioRepositoryImpl(
  private val dao: RadioStationDao,
  private val remoteDataSource: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : RadioRepository {

  private val mapper = RadioDtoMapper()

  override suspend fun count(): Long {
    return withContext(dispatchers.database) { dao.count() }
  }

  override fun getAll(): DataSource.Factory<Int, RadioStationEntity> {
    return dao.getAll()
  }

  override suspend fun getRemote() {
    val added = epoch()
    remoteDataSource.getAllPages(Protocol.RadioStations, RadioStationDto::class).blockingForEach { page ->
      runBlocking(dispatchers.disk) {
        val items = page.map { mapper.map(it).apply { dateAdded = added } }

        withContext(dispatchers.database) {
          dao.insertAll(items)
        }
      }
    }

    withContext(dispatchers.database) {
      dao.removePreviousEntries(added)
    }
  }

  override fun search(term: String): DataSource.Factory<Int, RadioStationEntity> {
    return dao.search(term)
  }

  override suspend fun cacheIsEmpty(): Boolean {
    return withContext(dispatchers.database) { dao.count() == 0L }
  }
}