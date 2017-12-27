package com.kelsos.mbrc.content.radios

import androidx.paging.PagingData
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RadioRepositoryImpl
@Inject
constructor(
  private val dao: RadioStationDao,
  private val remoteDataSource: RemoteRadioDataSource,
  private val dispatchers: AppDispatchers
) : RadioRepository {
  private val mapper = RadioDtoMapper()

  override suspend fun getAll(): Flow<PagingData<RadioStation>> =
    dao.getAll().paged()

  override suspend fun getAndSaveRemote(): Flow<PagingData<RadioStation>> {
    getRemote()
    return dao.getAll().paged()
  }

  override suspend fun getRemote() {
    val added = epoch()
    withContext(dispatchers.io) {
      remoteDataSource.fetch().onCompletion {
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
