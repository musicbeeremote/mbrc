package com.kelsos.mbrc.interfaces.data

import androidx.paging.DataSource
import arrow.core.Try

interface Repository<T> {
  fun getAll(): DataSource.Factory<Int, T>
  suspend fun getRemote(): Try<Unit>
  fun search(term: String): DataSource.Factory<Int, T>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
}