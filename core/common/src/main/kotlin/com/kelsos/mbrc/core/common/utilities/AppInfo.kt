package com.kelsos.mbrc.core.common.utilities

/**
 * Provides application build and version information.
 * Implementation should be provided by the app module.
 */
interface AppInfo {
  val versionName: String
  val versionCode: Int
  val buildTime: String
  val gitRevision: String
  val applicationId: String
}
