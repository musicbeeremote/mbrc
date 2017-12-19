package com.kelsos.mbrc.content.radios

import android.arch.paging.DataSource
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RadioRepositoryImpl
@Inject
constructor(
    private val dao: RadioStationDao,
    private val remoteDataSource: RemoteRadioDataSource
) : RadioRepository {
  private val mapper = RadioDtoMapper()

  override fun getAll(): Single<DataSource.Factory<Int, RadioStationEntity>> {
    return Single.just(dao.getAll())
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, RadioStationEntity>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.fetch().doOnNext {
      dao.insertAll(it.map { mapper.map(it).apply { dateAdded = added } })
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, RadioStationEntity>> {
    return Single.just(dao.search(term))
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.just(dao.count() == 0L)
  }
}
