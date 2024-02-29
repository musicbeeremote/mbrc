package com.kelsos.mbrc.utils

import androidx.paging.DifferCallback
import androidx.paging.NullPaddedList
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import arrow.core.Either
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun Either<Throwable, Unit>.result(): Any = fold({ it }, {})

suspend inline fun <T : Any> PagingData<T>.collectDataForTest(testScope: TestScope): List<T> {
  val items = mutableListOf<T>()
  val latch = CountDownLatch(1)
  val dcb = object : DifferCallback {
    override fun onChanged(position: Int, count: Int) = Unit
    override fun onInserted(position: Int, count: Int) = Unit
    override fun onRemoved(position: Int, count: Int) = Unit
  }
  val dif = object : PagingDataDiffer<T>(dcb, mainTestDispatcher) {
    override fun postEvents(): Boolean = true
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

  dif.retry()

  runCatching { latch.await(10, TimeUnit.SECONDS) }
  job.cancel()

  return items
}
