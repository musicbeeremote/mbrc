package com.kelsos.mbrc.interfaces.data

import com.raizlabs.android.dbflow.list.FlowCursorList

interface LocalDataSource<T : Data> {
  suspend fun deleteAll()
  suspend fun saveAll(list: List<T>)
  suspend fun loadAllCursor(): FlowCursorList<T>
  suspend fun search(term: String): FlowCursorList<T>
  suspend fun isEmpty(): Boolean
  suspend fun count(): Long
  suspend fun removePreviousEntries(epoch: Long)
}
