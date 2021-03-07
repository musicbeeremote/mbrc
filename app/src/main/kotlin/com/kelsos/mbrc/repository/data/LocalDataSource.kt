package com.kelsos.mbrc.repository.data

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.Model

interface LocalDataSource<T : Model> {
  suspend fun deleteAll()
  suspend fun saveAll(list: List<T>)
  suspend fun loadAllCursor(): FlowCursorList<T>
  suspend fun search(term: String): FlowCursorList<T>
  suspend fun isEmpty(): Boolean
  suspend fun count(): Long
  suspend fun removePreviousEntries(epoch: Long)
}
