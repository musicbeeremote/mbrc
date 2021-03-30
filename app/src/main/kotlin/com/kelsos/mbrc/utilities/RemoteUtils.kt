package com.kelsos.mbrc.utilities

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.pm.PackageInfoCompat
import rx.Emitter
import rx.Observable
import java.io.File
import java.security.MessageDigest

object RemoteUtils {

  @Throws(PackageManager.NameNotFoundException::class)
  fun Context.getVersion(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName
  }

  @Throws(PackageManager.NameNotFoundException::class)
  fun Context.getVersionCode(): Long {
    return PackageInfoCompat.getLongVersionCode(packageManager.getPackageInfo(packageName, 0))
  }

  fun bitmapFromFile(path: String): Observable<Bitmap> {
    return Observable.fromEmitter<Bitmap>({
      try {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val bitmap = BitmapFactory.decodeFile(path, options)
        if (bitmap != null) {
          it.onNext(bitmap)
          it.onCompleted()
        } else {
          it.onError(RuntimeException("Unable to decode the image"))
        }

      } catch (e: Exception) {
        it.onError(e)
      }
    }, Emitter.BackpressureMode.LATEST)
  }

  fun coverBitmap(coverPath: String): Observable<Bitmap> {
    val cover = File(coverPath)
    return bitmapFromFile(cover.absolutePath)
  }

  fun coverBitmapSync(coverPath: String): Bitmap? {
    return try {
      RemoteUtils.coverBitmap(coverPath).toBlocking().first()
    } catch (e: Exception) {
      null
    }
  }

  fun sha1(input: String) = hashString("SHA-1", input)

  private fun hashString(type: String, input: String): String {
    val HEX_CHARS = "0123456789ABCDEF"
    val bytes = MessageDigest
      .getInstance(type)
      .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
      val i = it.toInt()
      result.append(HEX_CHARS[i shr 4 and 0x0f])
      result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
  }
}
