package com.kelsos.mbrc.utilities

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import org.threeten.bp.Instant

fun <T> DataSource.Factory<Int, T>.paged(): LiveData<PagedList<T>> {
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
