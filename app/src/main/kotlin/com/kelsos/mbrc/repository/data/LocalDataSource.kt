package com.kelsos.mbrc.repository.data

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.Model
import rx.Observable
import rx.Single

interface LocalDataSource<OUT : Model, in IN: Model> {
  fun deleteAll()
  fun saveAll(list: List<IN>)
  fun loadAllCursor(): Observable<FlowCursorList<OUT>>
  fun search(term: String): Single<FlowCursorList<OUT>>
}
