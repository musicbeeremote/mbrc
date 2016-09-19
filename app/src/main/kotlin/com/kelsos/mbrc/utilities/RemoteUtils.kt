package com.kelsos.mbrc.utilities

import android.content.Context
import android.content.pm.PackageManager
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
}
