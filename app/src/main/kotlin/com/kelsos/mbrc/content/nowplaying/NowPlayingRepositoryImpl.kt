package com.kelsos.mbrc.content.nowplaying

import android.arch.paging.DataSource
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class NowPlayingRepositoryImpl
@Inject
constructor(
    private val remoteDataSource: RemoteNowPlayingDataSource,
    private val dao: NowPlayingDao
) : NowPlayingRepository {

  private val mapper = NowPlayingDtoMapper()

  override fun getAll(): Single<DataSource.Factory<Int, NowPlayingEntity>> {
    return Single.just(dao.getAll())
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, NowPlayingEntity>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.fetch().doOnNext {
      val list = it.map { mapper.map(it).apply { dateAdded = added } }
      dao.insertAll(list)
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, NowPlayingEntity>> {
    return Single.just(dao.search(term))
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.just(dao.count() == 0L)
}
