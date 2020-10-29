package com.kelsos.mbrc.utilities

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import rx.Emitter
import rx.Observable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object RemoteUtils {

  @Throws(PackageManager.NameNotFoundException::class)
  fun getVersion(mContext: Context): String {
    val mInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
    return mInfo.versionName
  }

  @Throws(PackageManager.NameNotFoundException::class)
  fun getVersionCode(mContext: Context): Long {
    val mInfo = mContext.packageManager.getPackageInfo(mContext.packageName, 0)
    return mInfo.versionCode.toLong()
  }

  /**
   * Retrieves the current ISO formatted DateTime.

   * @return Time at this moment in ISO 8601 format
   */
  val utcNow: String
    get() {
      val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
      df.timeZone = TimeZone.getTimeZone("UTC")
      return df.format(Date())
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
}
