package com.kelsos.mbrc.utilities

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import org.threeten.bp.Instant

fun <T : Any> DataSource.Factory<Int, T>.paged(): LiveData<PagedList<T>> {
  val config = PagedList.Config.Builder()
    .setPageSize(40)
    .setEnablePlaceholders(true)
    .build()
  return LivePagedListBuilder(this, config).build()
}

/**
 * [Instant.getEpochSecond] for [Instant.now]
 */
fun epoch(): Long = Instant.now().epochSecond
