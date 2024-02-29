package com.kelsos.mbrc.common.data

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingData
import arrow.core.Either
import kotlinx.coroutines.flow.Flow

typealias Progress = suspend (current: Int, total: Int) -> Unit

interface Repository<T : Any> {
  fun getAll(): Flow<PagingData<T>>
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun all(): List<T>
  suspend fun getRemote(progress: Progress = { _, _ -> }): Either<Throwable, Unit>
  fun search(term: String): Flow<PagingData<T>>
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun simpleSearch(term: String): List<T>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
  suspend fun getById(id: Long): T?
}
