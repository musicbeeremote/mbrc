package com.kelsos.mbrc.interfaces.data

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface Repository<T : Data> {
  fun getAll(): Flow<PagingData<T>>
  suspend fun getRemote()
  fun search(term: String): Flow<PagingData<T>>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
}
