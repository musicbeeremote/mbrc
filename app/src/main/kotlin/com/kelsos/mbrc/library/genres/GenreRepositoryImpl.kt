package com.kelsos.mbrc.library.genres

import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GenreRepositoryImpl
@Inject constructor(private val remoteDataSource: RemoteGenreDataSource,
                    private val localDataSource: LocalGenreDataSource) : GenreRepository {
  override fun getAllCursor(): Single<FlowCursorList<Genre>> {
    return localDataSource.loadAllCursor().firstOrError()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Genre>> {
    return getRemote().andThen(localDataSource.loadAllCursor().firstOrError())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.ignoreElements()
  }

  override fun search(term: String): Single<FlowCursorList<Genre>> {
    return localDataSource.search(term)
  }

  override fun cacheIsEmpty(): Single<Boolean> {
    return localDataSource.isEmpty()
  }
}
