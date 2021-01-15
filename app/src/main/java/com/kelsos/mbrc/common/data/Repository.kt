package com.kelsos.mbrc.common.data

import androidx.paging.DataSource
import arrow.core.Either

typealias Progress = suspend (current: Int, total: Int) -> Unit

interface Repository<T> {
  fun getAll(): DataSource.Factory<Int, T>
  suspend fun getRemote(progress: Progress = { _, _ -> }): Either<Throwable, Unit>
  fun search(term: String): DataSource.Factory<Int, T>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
  suspend fun getById(id: Long): T?
}
