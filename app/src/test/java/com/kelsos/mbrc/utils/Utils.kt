package com.kelsos.mbrc.utils

import androidx.paging.DifferCallback
import androidx.paging.NullPaddedList
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import arrow.core.Either
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun Either<Throwable, Unit>.result(): Any = fold({ it }, {})

suspend fun <T : Any> PagingData<T>.collectDataForTest(): List<T> {
  val latch = CountDownLatch(1)
  val dcb = object : DifferCallback {
    override fun onChanged(position: Int, count: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
  }
  val items = mutableListOf<T>()
  val dif = object : PagingDataDiffer<T>(dcb, testDispatcher) {
    override suspend fun presentNewList(
      previousList: NullPaddedList<T>,
      newList: NullPaddedList<T>,
      lastAccessedIndex: Int,
      onListPresentable: () -> Unit
    ): Int? {
      for (idx in 0 until newList.size)
        items.add(newList.getFromStorage(idx))
      onListPresentable()
      latch.countDown()
      return null
    }
  }

  val job = testScope.launch {
    dif.collectFrom(this@collectDataForTest)
  }

  val awaitResult = kotlin.runCatching {
    latch.await(30, TimeUnit.SECONDS)
  }

  if (awaitResult.isFailure) {
    Timber.e(awaitResult.exceptionOrNull())
  }

  job.cancel()

  return items
}
