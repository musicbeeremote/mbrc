package com.kelsos.mbrc.content.radios

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

class RadioRepositoryImpl(
  private val dao: RadioStationDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : RadioRepository {
  private val mapper = RadioDtoMapper()

  override suspend fun getAll(): Flow<PagingData<RadioStation>> =
    dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<RadioStation>> {
    getRemote()
    return dao.getAll().paged()
  }

  override suspend fun getRemote() {
    withContext(dispatchers.network) {
      val added = epoch()
      api.getAllPages(Protocol.RadioStations, RadioStationDto::class)
        .onCompletion {
          dao.removePreviousEntries(added)
        }.collect { radios ->
          val data = radios.map { mapper.map(it).apply { dateAdded = added } }
          dao.insertAll(data)
        }
    }
  }

  override suspend fun search(
    term: String
  ): Flow<PagingData<RadioStation>> = dao.search(term).paged()

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
