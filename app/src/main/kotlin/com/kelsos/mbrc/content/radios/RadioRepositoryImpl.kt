package com.kelsos.mbrc.content.radios

import android.arch.lifecycle.LiveData
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

  override fun getAll(): Single<LiveData<List<RadioStationEntity>>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getAndSaveRemote(): Single<LiveData<List<RadioStationEntity>>> {
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

  override fun search(term: String): Single<LiveData<List<RadioStationEntity>>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return Single.fromCallable { dao.count() == 0L }
  }
}
