package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.repository.data.LocalGenreDataSource
import com.kelsos.mbrc.repository.data.RemoteGenreDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class GenreRepositoryImpl
@Inject constructor(private val remoteDataSource: RemoteGenreDataSource,
                    private val localDataSource: LocalGenreDataSource) : GenreRepository {
  override fun getAllCursor(): Single<FlowCursorList<Genre>> {
    return localDataSource.loadAllCursor().toSingle()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Genre>> {
    return getRemote().andThen(localDataSource.loadAllCursor().toSingle())
  }

  override fun getRemote(): Completable {
    localDataSource.deleteAll()
    return remoteDataSource.fetch().doOnNext {
      localDataSource.saveAll(it)
    }.toCompletable()
  }

  override fun search(term: String): Single<FlowCursorList<Genre>> {
    return localDataSource.search(term)
  }
}
