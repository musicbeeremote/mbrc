package com.kelsos.mbrc.content.radios

import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RadioRepositoryImpl
@Inject constructor(
    private val localDataSource: LocalRadioDataSource,
    private val remoteDataSource: RemoteRadioDataSource
) : RadioRepository {
  override fun getAllCursor(): Single<List<RadioStation>> {
    return localDataSource.loadAllCursor().firstOrError()
  }

  override fun getAndSaveRemote(): Single<List<RadioStation>> {
    return getRemote().andThen(localDataSource.loadAllCursor().firstOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<List<RadioStation>> {
    return localDataSource.search(term)
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
