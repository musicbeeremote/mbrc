package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.Data
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Observable
import io.reactivex.Single

interface LocalDataSource<T : Data> {
  fun deleteAll()
  fun saveAll(list: List<T>)
  fun loadAllCursor(): Observable<FlowCursorList<T>>
  fun search(term: String): Single<FlowCursorList<T>>
  fun isEmpty(): Single<Boolean>
}
