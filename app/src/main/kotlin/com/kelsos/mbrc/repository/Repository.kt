package com.kelsos.mbrc.repository

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.Model
import rx.Completable
import rx.Single

interface Repository<T : Model> {
  fun getAllCursor(): Single<FlowCursorList<T>>
  fun getAndSaveRemote(): Single<FlowCursorList<T>>
  fun getRemote(): Completable
  fun search(term: String): Single<FlowCursorList<T>>
  fun cacheIsEmpty() : Single<Boolean>
}
