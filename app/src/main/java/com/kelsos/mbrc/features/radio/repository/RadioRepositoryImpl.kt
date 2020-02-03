package com.kelsos.mbrc.features.radio.repository

import androidx.paging.PagingData
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.epoch
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.radio.RadioStationDto
import com.kelsos.mbrc.features.radio.data.RadioStationDao
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.features.radio.toEntity
import com.kelsos.mbrc.features.radio.toRadioStation
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext

class RadioRepositoryImpl(
  private val dao: RadioStationDao,
  private val api: ApiBase,
  private val dispatchers: AppCoroutineDispatchers
) : RadioRepository {

  override suspend fun count(): Long = withContext(dispatchers.database) {
    dao.count()
  }

  override fun getAll(): Flow<PagingData<RadioStation>> =
    dao.getAll().paged { it.toRadioStation() }

  override suspend fun getRemote(): Try<Unit> = Try {
    withContext(dispatchers.network) {
      val added = epoch()
      api.getAllPages(Protocol.RadioStations, RadioStationDto::class)
        .onCompletion {
          withContext(dispatchers.database) {
            dao.removePreviousEntries(added)
          }
        }.collect { radios ->
          val data = radios.map { it.toEntity().apply { dateAdded = added } }
          withContext(dispatchers.database) {
            withContext(dispatchers.database) {
              dao.insertAll(data)
            }
          }
        }
    }
  }

  override fun search(
    term: String
  ): Flow<PagingData<RadioStation>> = dao.search(term).paged { it.toRadioStation() }

  override suspend fun cacheIsEmpty(): Boolean =
    withContext(dispatchers.database) { dao.count() == 0L }

  override suspend fun getById(id: Long): RadioStation? {
    return withContext(dispatchers.database) {
      val entity = dao.getById(id) ?: return@withContext null
      return@withContext entity.toRadioStation()
    }
  }
}
