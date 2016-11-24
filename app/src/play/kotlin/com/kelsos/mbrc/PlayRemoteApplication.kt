package com.kelsos.mbrc

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class PlayRemoteApplication : RemoteApplication() {
  override fun onCreate() {
    super.onCreate()
    Fabric.with(this, Crashlytics())
  }
}
