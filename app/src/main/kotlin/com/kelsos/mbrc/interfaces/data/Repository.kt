package com.kelsos.mbrc.interfaces.data

import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Completable
import io.reactivex.Single

interface Repository<T : Data> {
  fun getAllCursor(): Single<FlowCursorList<T>>
  fun getAndSaveRemote(): Single<FlowCursorList<T>>
  fun getRemote(): Completable
  fun search(term: String): Single<FlowCursorList<T>>
  fun cacheIsEmpty() : Single<Boolean>
}
