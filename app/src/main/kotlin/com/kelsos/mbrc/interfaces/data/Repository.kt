package com.kelsos.mbrc.interfaces.data

import io.reactivex.Completable
import io.reactivex.Single

interface Repository<T : Data> {
  fun getAllCursor(): Single<List<T>>
  fun getAndSaveRemote(): Single<List<T>>
  fun getRemote(): Completable
  fun search(term: String): Single<List<T>>
  fun cacheIsEmpty() : Single<Boolean>
}
