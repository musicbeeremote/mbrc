package com.kelsos.mbrc.content.radios

import android.arch.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class RadioRepositoryImpl
@Inject
constructor(
  private val dao: RadioStationDao,
  private val remoteDataSource: ApiBase,
  private val coroutineDispatchers: AppCoroutineDispatchers
) : RadioRepository {

  private val mapper = RadioDtoMapper()

  override suspend fun count(): Long {
    return withContext(coroutineDispatchers.database) { dao.count() }
  }

  override fun getAll(): Single<DataSource.Factory<Int, RadioStationEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.getAllPages(Protocol.RadioStations, RadioStationDto::class).doOnNext {
      async(CommonPool) {
        val items = it.map { mapper.map(it).apply { dateAdded = added } }

        withContext(coroutineDispatchers.database) {
          dao.insertAll(items)
        }
      }

    }.doOnComplete {
      async(coroutineDispatchers.database) {
        dao.removePreviousEntries(added)
      }
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, RadioStationEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.fromCallable { dao.count() == 0L }
  }
}