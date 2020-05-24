package com.kelsos.mbrc.common.data

import androidx.paging.PagingData
import arrow.core.Either
import kotlinx.coroutines.flow.Flow

typealias Progress = suspend (current: Int, total: Int) -> Unit

interface Repository<T : Any> {
  fun getAll(): Flow<PagingData<T>>
  suspend fun getRemote(progress: Progress = { _, _ -> }): Either<Throwable, Unit>
  fun search(term: String): Flow<PagingData<T>>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
  suspend fun getById(id: Long): T?
}
