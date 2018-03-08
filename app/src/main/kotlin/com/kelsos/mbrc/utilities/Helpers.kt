package com.kelsos.mbrc.utilities

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant

fun <T, I : Any> PagingSource<Int, T>.paged(): Flow<PagingData<I>> where T : I {
  val config = PagingConfig(
    prefetchDistance = 100,
    enablePlaceholders = true,
    initialLoadSize = 100,
    pageSize = 50
  )
  return Pager(config) { this }.flow.map { data -> data.map { it } }
}

/**
 * [Instant.getEpochSecond] for [Instant.now]
 */
fun epoch(): Long = Instant.now().epochSecond
