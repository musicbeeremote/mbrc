package com.kelsos.mbrc.adapters

import com.kelsos.mbrc.BuildConfig
import com.kelsos.mbrc.core.common.utilities.AppInfo

class AppInfoImpl : AppInfo {
  override val versionName: String = BuildConfig.VERSION_NAME
  override val versionCode: Int = BuildConfig.VERSION_CODE
  override val buildTime: String = BuildConfig.BUILD_TIME
  override val gitRevision: String = BuildConfig.GIT_SHA
  override val applicationId: String = BuildConfig.APPLICATION_ID
}
