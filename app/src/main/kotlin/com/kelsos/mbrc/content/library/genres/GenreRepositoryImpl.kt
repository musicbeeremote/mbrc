package com.kelsos.mbrc.content.library.genres

import android.arch.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
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

class GenreRepositoryImpl
@Inject
constructor(
  private val remoteDataSource: ApiBase,
  private val dao: GenreDao,
  private val coroutineDispatchers: AppCoroutineDispatchers
) : GenreRepository {

  private val mapper = GenreDtoMapper()

  override suspend fun count(): Long {
    return withContext(coroutineDispatchers.database) { dao.count() }
  }

  override fun getAll(): Single<DataSource.Factory<Int, GenreEntity>> {
    return Single.fromCallable { dao.getAll() }
  }

  override fun getRemote(): Completable {
    val added = epoch()
    return remoteDataSource.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class).doOnNext {
      async(CommonPool) {
        val items = it.map { mapper.map(it).apply { dateAdded = added } }
        withContext(coroutineDispatchers.database) {
          dao.saveAll(items)
        }
      }
    }.doOnComplete {
      async(coroutineDispatchers.database) {
        dao.removePreviousEntries(added)
      }
    }.ignoreElements()
  }

  override fun search(term: String): Single<DataSource.Factory<Int, GenreEntity>> {
    return Single.fromCallable { dao.search(term) }
  }

  override fun cacheIsEmpty(): Single<Boolean> = Single.fromCallable { dao.count() == 0L }

  override fun allGenres(): Single<DataModel<GenreEntity>> {
    return Single.fromCallable { DataModel(dao.getAll(), dao.getAllIndexes()) }
  }
}