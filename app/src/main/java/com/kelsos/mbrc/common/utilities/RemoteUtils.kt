package com.kelsos.mbrc.common.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.kelsos.mbrc.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

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

  fun loadBitmap(path: String): Bitmap? = BitmapFactory.decodeFile(
    path,
    BitmapFactory.Options().apply {
      inPreferredConfig = Bitmap.Config.RGB_565
    }
  )

  private suspend fun coverBitmap(coverPath: String): Bitmap? {
    val cover = File(coverPath)
    return bitmapFromFile(cover.absolutePath)
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
