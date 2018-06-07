package com.kelsos.mbrc.interfaces.data

import androidx.paging.DataSource
import io.reactivex.Completable
import io.reactivex.Single

interface Repository<T : Data> {
  fun getAll(): Single<DataSource.Factory<Int, T>>
  fun getRemote(): Completable
  fun search(term: String): Single<DataSource.Factory<Int, T>>
  fun cacheIsEmpty(): Single<Boolean>
  suspend fun count(): Long
}