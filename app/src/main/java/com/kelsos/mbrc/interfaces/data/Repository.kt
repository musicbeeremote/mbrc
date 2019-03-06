package com.kelsos.mbrc.interfaces.data

import androidx.paging.DataSource

interface Repository<T> {
  fun getAll(): DataSource.Factory<Int, T>
  suspend fun getRemote()
  fun search(term: String): DataSource.Factory<Int, T>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
}