package com.kelsos.mbrc.interfaces.data

import android.arch.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Single

interface Repository<T : Data> {
  fun getAll(): Single<LiveData<List<T>>>
  fun getAndSaveRemote(): Single<LiveData<List<T>>>
  fun getRemote(): Completable
  fun search(term: String): Single<LiveData<List<T>>>
  fun cacheIsEmpty() : Single<Boolean>
}
