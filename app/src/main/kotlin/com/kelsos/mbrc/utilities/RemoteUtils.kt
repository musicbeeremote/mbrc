package com.kelsos.mbrc.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import arrow.core.Option
import arrow.core.Try
import com.kelsos.mbrc.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.security.MessageDigest

object RemoteUtils {

  fun getVersion(): String {
    return BuildConfig.VERSION_NAME
  }

  fun getVersionCode(): Int {
    return BuildConfig.VERSION_CODE
  }

  private fun bitmapFromFile(path: String): Bitmap? = runBlocking {
    return@runBlocking try {
      withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        BitmapFactory.decodeFile(path, options)
      }
    } catch (e: Exception) {
      Timber.v(e)
      null
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

  private fun coverBitmap(coverPath: String): Bitmap? {
    val cover = File(coverPath)
    return bitmapFromFile(cover.absolutePath)
  }

  fun coverBitmapSync(coverPath: String): Bitmap? = try {
    coverBitmap(coverPath)
  } catch (e: Exception) {
    null
  }

  fun sha1(input: String) = hashString("SHA-1", input)

  private fun hashString(type: String, input: String): String {
    val hexChars = "0123456789ABCDEF"
    val bytes = MessageDigest
      .getInstance(type)
      .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
      val i = it.toInt()
      result.append(hexChars[i shr 4 and 0x0f])
      result.append(hexChars[i and 0x0f])
    }

    return result.toString()
  }
}
