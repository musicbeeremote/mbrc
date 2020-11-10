package com.kelsos.mbrc.common.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Option
import arrow.core.Try
import com.kelsos.mbrc.BuildConfig
import io.reactivex.Observable
import java.io.File

object RemoteUtils {

  fun getVersion(): String {
    return BuildConfig.VERSION_NAME
  }

  fun getVersionCode(): Int {
    return BuildConfig.VERSION_CODE
  }

  private fun bitmapFromFile(path: String): Observable<Bitmap> {
    return Observable.create<Bitmap> {
      try {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val bitmap = BitmapFactory.decodeFile(path, options)
        if (bitmap != null) {
          it.onNext(bitmap)
          it.onComplete()
        } else {
          it.onError(RuntimeException("Unable to decode the image"))
        }
      } catch (e: Exception) {
        it.onError(e)
      }
    }
  }

  fun loadBitmap(path: String): Option<Bitmap> {
    return Try {
      BitmapFactory.decodeFile(
        path,
        BitmapFactory.Options().apply {
          inPreferredConfig = Bitmap.Config.RGB_565
        }
      )
    }.toOption()
  }

  private fun coverBitmap(coverPath: String): Observable<Bitmap> {
    val cover = File(coverPath)
    return bitmapFromFile(cover.absolutePath)
  }

  fun coverBitmapSync(coverPath: String): Bitmap? {
    return try {
      RemoteUtils.coverBitmap(coverPath).blockingLast()
    } catch (e: Exception) {
      null
    }
  }
}