package com.kelsos.mbrc.content.nowplaying

import android.arch.paging.DataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class NowPlayingRepositoryImpl
@Inject
constructor(
  private val remoteDataSource: ApiBase,
  private val dao: NowPlayingDao
) : NowPlayingRepository {

  private val mapper = NowPlayingDtoMapper()

  override fun getAll(): Single<DataSource.Factory<Int, NowPlayingEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, NowPlayingEntity>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.getAllPages(Protocol.NowPlayingList, NowPlayingDto::class).doOnNext {
      val list = it.map { mapper.map(it).apply { dateAdded = added } }
      dao.insertAll(list)
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.ignoreElements().doOnError { Timber.v(it) }
  }

  override fun search(term: String): Single<DataSource.Factory<Int, NowPlayingEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.fromCallable { dao.count() == 0L }

  override fun move(from: Int, to: Int) {
    TODO("implement move")
  }
}