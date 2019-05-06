package com.kelsos.mbrc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import androidx.room.Room
import arrow.core.Either
import arrow.core.Try
import com.kelsos.mbrc.data.Database
import com.kelsos.mbrc.features.nowplaying.NowPlayingDto
import com.kelsos.mbrc.features.playlists.PlaylistDto
import io.reactivex.Observable
import java.io.File
import java.io.FileOutputStream

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

object ImageCreator {
  private val redPaint: Paint = Paint().apply { setARGB(255, 255, 0, 0) }

  fun create(): Either<Throwable, String> = Try {
    val width = 100
    val height = 100

    val path = File.createTempFile("cover-", ".png")
    val bmp = Bitmap.createBitmap(
      width,
      height,
      Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bmp)
    val radius: Float = Math.min(width, height).toFloat() / 2f
    canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, redPaint)

    FileOutputStream(path).use { out ->
      bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    bmp.recycle()
    path.absolutePath
  }.toEither()
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
}