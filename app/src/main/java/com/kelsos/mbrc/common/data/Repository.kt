package com.kelsos.mbrc.common.data

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingData
import arrow.core.Either
import kotlinx.coroutines.flow.Flow

typealias Progress = suspend (current: Int, total: Int) -> Unit

interface TestApi<T : Any> {
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun getAll(): List<T>

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  fun search(term: String): List<T>
}

interface Repository<T : Any> {
  fun getAll(): Flow<PagingData<T>>

  suspend fun getRemote(progress: Progress = { _, _ -> }): Either<Throwable, Unit>

  fun search(term: String): Flow<PagingData<T>>

  suspend fun count(): Long

  suspend fun getById(id: Long): T?

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  val test: TestApi<T>
}

suspend fun <T : Any> Repository<T>.cacheIsEmpty(): Boolean = count() == 0L
