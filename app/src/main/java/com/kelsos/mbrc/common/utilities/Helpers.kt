package com.kelsos.mbrc.common.utilities

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

fun <T : Any, I : Any> paged(
  pagingSourceFactory: () -> PagingSource<Int, T>,
  transform: (value: T) -> I
): Flow<PagingData<I>> {
  val config = PagingConfig(
    enablePlaceholders = true,
    pageSize = 60,
    maxSize = 200
  )
  return Pager(
    config,
    pagingSourceFactory = pagingSourceFactory
  ).flow.map { data -> data.map(transform) }
}

/**
 * [Instant.getEpochSecond] for [Instant.now]
 */
fun epoch(): Long = Instant.now().epochSecond
