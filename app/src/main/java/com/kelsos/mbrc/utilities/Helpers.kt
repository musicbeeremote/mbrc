package com.kelsos.mbrc.utilities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import org.threeten.bp.Instant

fun <T> DataSource.Factory<Int, T>.paged(): LiveData<PagedList<T>> {
  val config = PagedList.Config.Builder()
    .setPrefetchDistance(100)
    .setInitialLoadSizeHint(25)
    .setPageSize(100)
    .setEnablePlaceholders(true)
    .build()
  return LivePagedListBuilder(this, config).build()
}

fun <T> LiveData<T>.nonNullObserver(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
  observe(lifecycleOwner, Observer {
    if (it == null) {
      return@Observer
    }

    observer(it)
  })
}

/**
 * [Instant.getEpochSecond] for [Instant.now]
 */
fun epoch(): Long = Instant.now().epochSecond