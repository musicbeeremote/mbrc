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
        callback.onResult(data.subList(
          params.startPosition,
          params.startPosition + params.loadSize
        ))
      }

      override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        val page = data.subList(
          params.requestedStartPosition,
          params.requestedStartPosition + params.requestedLoadSize
        )
        callback.onResult(page, 0, page.size)
      }
    }
  }
}