package com.kelsos.mbrc.utils

import android.content.Context
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import androidx.room.Room
import com.kelsos.mbrc.data.Database
import io.reactivex.Observable

object TestData {
  fun createDB(context: Context): Database =
    Room.inMemoryDatabaseBuilder(context, Database::class.java)
      .allowMainThreadQueries()
      .build()

  fun <T> mockApi(
    count: Int,
    inject: List<T> = emptyList(),
    make: (position: Int) -> T
  ): Observable<List<T>> {
    return Observable.range(0, count)
      .map { make(it) }
      .mergeWith(Observable.fromIterable(inject))
      .toList()
      .toObservable()
  }
}

class MockFactory<T>(private val data: List<T>) : DataSource.Factory<Int, T>() {
  override fun create(): DataSource<Int, T> {
    return object : PositionalDataSource<T>() {
      override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        val start = params.startPosition
        val end = start + params.loadSize
        callback.onResult(subList(start, end))
      }

      override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        val start = params.requestedStartPosition
        val end = start + params.requestedLoadSize
        val page = subList(start, end)
        callback.onResult(page, 0, page.size)
      }
    }
  }

  private fun subList(start: Int, end: Int): List<T> {
    return when {
      data.isEmpty() -> emptyList()
      end >= data.size -> data.subList(start, data.size)
      else -> data.subList(start, end)
    }
  }
}