package com.kelsos.mbrc.common.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Either
import com.kelsos.mbrc.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object RemoteUtils {

  fun getVersion(): String {
    return BuildConfig.VERSION_NAME
  }

  fun getVersionCode(): Int {
    return BuildConfig.VERSION_CODE
  }

  private suspend fun bitmapFromFile(path: String): Bitmap = withContext(Dispatchers.IO) {
    val options = BitmapFactory.Options()
    options.inPreferredConfig = Bitmap.Config.RGB_565
    return@withContext BitmapFactory.decodeFile(path, options)
      ?: throw RuntimeException("Unable to decode the image")
  }

  suspend fun loadBitmap(path: String): Either<Throwable, Bitmap> = Either.catch {
    BitmapFactory.decodeFile(
      path,
      BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.RGB_565
      }
    )
  }

  suspend fun coverBitmap(coverPath: String): Bitmap? {
    return try {
      bitmapFromFile(File(coverPath).absolutePath)
    } catch (e: Exception) {
      null
    }
  }
}
