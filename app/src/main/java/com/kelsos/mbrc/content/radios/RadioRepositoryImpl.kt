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

  override suspend fun count(): Long = withContext(dispatchers.database) { dao.count() }

  override fun getAll(): Flow<PagingData<RadioStation>> =
    dao.getAll().paged { it.toRadioStation() }

  override suspend fun getRemote() {
    withContext(dispatchers.network) {
      val added = epoch()
      api.getAllPages(Protocol.RadioStations, RadioStationDto::class)
        .onCompletion {
          dao.removePreviousEntries(added)
        }.collect { radios ->
          val data = radios.map { it.toEntity().apply { dateAdded = added } }
          withContext(dispatchers.database) {
            dao.insertAll(data)
          }
        }
    }
  }

  override fun search(
    term: String
  ): Flow<PagingData<RadioStation>> = dao.search(term).paged { it.toRadioStation() }

  override suspend fun cacheIsEmpty(): Boolean =
    withContext(dispatchers.database) { dao.count() == 0L }
}
