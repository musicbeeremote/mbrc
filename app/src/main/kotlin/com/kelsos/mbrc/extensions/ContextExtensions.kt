package com.kelsos.mbrc.extensions

import android.content.Context
import android.content.pm.PackageManager


val Context.version: String
  @Throws(PackageManager.NameNotFoundException::class)
  get() {
    val mInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return mInfo.versionName
  }


val Context.versionCode: Long
  @Throws(PackageManager.NameNotFoundException::class)
  get() {
    val mInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return mInfo.versionCode.toLong()
  }
