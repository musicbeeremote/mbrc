package com.kelsos.mbrc.common.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Either
import com.kelsos.mbrc.BuildConfig

object RemoteUtils {
  val version: String
    get() {
      return BuildConfig.VERSION_NAME
    }

  val versionCode: Int
    get() {
      return BuildConfig.VERSION_CODE
    }

  fun loadBitmap(path: String): Either<Throwable, Bitmap> =
    Either.catch {
      BitmapFactory.decodeFile(
        path,
        BitmapFactory.Options().apply {
          inPreferredConfig = Bitmap.Config.RGB_565
        },
      )
    }
}
