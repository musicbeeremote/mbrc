package com.kelsos.mbrc.repository.data

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.Model
import rx.Observable

interface LocalDataSource<T : Model> {
  fun deleteAll()
  fun saveAll(list: List<T>)
  fun loadAllCursor(): Observable<FlowCursorList<T>>
}
