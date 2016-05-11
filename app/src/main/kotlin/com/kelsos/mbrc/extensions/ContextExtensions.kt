package com.kelsos.mbrc.extensions

import android.content.Context
import android.content.pm.PackageManager
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager


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

/**
 * Initializes DbFlow with the current [Context]
 */
fun Context.initDBFlow() {

  val config = FlowConfig.Builder(this)
      .openDatabasesOnInit(true)
      .build()

  FlowManager.init(config)
}
