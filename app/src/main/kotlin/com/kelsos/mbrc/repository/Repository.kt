package com.kelsos.mbrc.repository

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.Model

interface Repository<T : Model> {
  suspend fun getAllCursor(): FlowCursorList<T>
  suspend fun getAndSaveRemote(): FlowCursorList<T>
  suspend fun getRemote()
  suspend fun search(term: String): FlowCursorList<T>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
}
