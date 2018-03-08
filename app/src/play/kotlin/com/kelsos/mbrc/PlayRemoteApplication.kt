package com.kelsos.mbrc

import com.crashlytics.android.Crashlytics
import com.kelsos.mbrc.BuildConfig.USE_CRASHLYTICS
import io.fabric.sdk.android.Fabric

class PlayRemoteApplication : RemoteApplication() {
  override fun onCreate() {
    super.onCreate()
    if (USE_CRASHLYTICS) {
      Fabric.with(this, Crashlytics())
    }
  }
}