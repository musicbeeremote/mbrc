package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.repository.data.LocalRadioDataSource
import com.kelsos.mbrc.repository.data.RemoteRadioDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RadioRepositoryImpl
@Inject constructor(
    private val localDataSource: LocalRadioDataSource,
    private val remoteDataSource: RemoteRadioDataSource
) : RadioRepository {
  override fun getAllCursor(): Single<FlowCursorList<RadioStation>> {
    return localDataSource.loadAllCursor().firstOrError()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<RadioStation>> {
    return getRemote().andThen(localDataSource.loadAllCursor().firstOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<FlowCursorList<RadioStation>> {
    return localDataSource.search(term)
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
