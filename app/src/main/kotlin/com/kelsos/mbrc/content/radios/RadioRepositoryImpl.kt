package com.kelsos.mbrc.content.radios

import androidx.paging.DataSource
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.utilities.epoch
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

  override suspend fun getAll(): DataSource.Factory<Int, RadioStation> =
    dao.getAll().map { it }

  override suspend fun getAndSaveRemote(): DataSource.Factory<Int, RadioStation> {
    getRemote()
    return dao.getAll().map { it }
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

  override suspend fun search(term: String): DataSource.Factory<Int, RadioStation> =
    dao.search(term).map { it }

  override suspend fun cacheIsEmpty(): Boolean = dao.count() == 0L

  override suspend fun count(): Long = dao.count()
}
