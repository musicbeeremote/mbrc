package com.kelsos.mbrc.content.library.genres

import android.arch.paging.DataSource
import com.kelsos.mbrc.utilities.epoch
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class GenreRepositoryImpl
@Inject
constructor(
    private val remoteDataSource: RemoteGenreDataSource,
    private val dao: GenreDao
) : GenreRepository {

  private val mapper = GenreDtoMapper()

  override fun getAll(): Single<DataSource.Factory<Int, GenreEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getAndSaveRemote(): Single<DataSource.Factory<Int, GenreEntity>> {
    return getRemote().andThen(getAll())
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.fetch().doOnNext {
      dao.saveAll(it.map { mapper.map(it).apply { dateAdded = added } })
    }.doOnComplete {
      dao.removePreviousEntries(added)
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, GenreEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.fromCallable { dao.count() == 0L }
}
