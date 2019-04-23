package com.kelsos.mbrc.utils

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Room
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object TestData {
  fun createDB(context: Context): Database =
    Room.inMemoryDatabaseBuilder(context, Database::class.java)
      .allowMainThreadQueries()
      .build()

  fun <T> mockApi(
    count: Int,
    inject: List<T> = emptyList(),
    make: (position: Int) -> T
  ): Flow<List<T>> = flow {
    emit((0 until count).map { make(it) } + inject)
  }
}

class MockFactory<T : Any>(private val data: List<T> = emptyList()) : PagingSource<Int, T>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
    return LoadResult.Page(
      data = data,
      prevKey = null,
      nextKey = null
    )
  }

  override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

  fun flow(): Flow<PagingData<T>> = this.paged { it }
}
