package com.kelsos.mbrc.utils

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Room
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.playlists.PlaylistDto
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

object TestDataFactories {
  fun playlist(num: Int): PlaylistDto = PlaylistDto(
    name = "Songs $num",
    url = """C:\library\$num.m3u"""
  )

  fun nowPlayingList(index: Int): NowPlayingDto = NowPlayingDto(
    title = "Song ${index + 1}",
    artist = "Artist",
    position = index + 1,
    path = """C:\library\album\${index + 1}.mp3"""
  )

  fun nowPlayingListEntities(total: Int): List<NowPlaying> {
    return (1..total + 1).map {
      NowPlaying(
        artist = "Test artist",
        title = "Test title $it",
        id = it.toLong(),
        position = it,
        path = """C:\library\album\$it.mp3"""
      )
    }
  }
}
