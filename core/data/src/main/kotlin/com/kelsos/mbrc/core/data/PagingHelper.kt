package com.kelsos.mbrc.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Any, I : Any> paged(
  pagingSourceFactory: () -> PagingSource<Int, T>,
  transform: (value: T) -> I
): Flow<PagingData<I>> {
  val config =
    PagingConfig(
      enablePlaceholders = false,
      pageSize = 50,
      prefetchDistance = 25,
      initialLoadSize = 100
    )
  return Pager(
    config,
    pagingSourceFactory = pagingSourceFactory
  ).flow.map { data -> data.map(transform) }
}
