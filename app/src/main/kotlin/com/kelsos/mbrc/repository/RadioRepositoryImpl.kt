package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.repository.data.LocalRadioDataSource
import com.kelsos.mbrc.repository.data.RemoteRadioDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RadioRepositoryImpl
@Inject constructor(
  private val localDataSource: LocalRadioDataSource,
  private val remoteDataSource: RemoteRadioDataSource,
  private val dispatchers: AppDispatchers
) : RadioRepository {
  override suspend fun getAllCursor(): FlowCursorList<RadioStation> =
    localDataSource.loadAllCursor()

  override suspend fun getAndSaveRemote(): FlowCursorList<RadioStation> {
    getRemote()
    return localDataSource.loadAllCursor()
  }

  override suspend fun getRemote() {
    localDataSource.deleteAll()
    withContext(dispatchers.io) {
      remoteDataSource.fetch().collect {
        localDataSource.saveAll(it)
      }
    }
  }

  override suspend fun search(term: String): FlowCursorList<RadioStation> =
    localDataSource.search(term)

  override suspend fun cacheIsEmpty(): Boolean = localDataSource.isEmpty()

  override suspend fun count(): Long = localDataSource.count()
}
