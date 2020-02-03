package com.kelsos.mbrc.common.data

import androidx.paging.PagingData
import arrow.core.Try
import kotlinx.coroutines.flow.Flow

interface Repository<T : Any> {
  fun getAll(): Flow<PagingData<T>>
  suspend fun getRemote(): Try<Unit>
  fun search(term: String): Flow<PagingData<T>>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
  suspend fun getById(id: Long): T?
}
