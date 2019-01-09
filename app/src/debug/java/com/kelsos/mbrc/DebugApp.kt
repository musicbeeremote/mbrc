package com.kelsos.mbrc

import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class DebugApp : FlavorApp() {
  override fun onCreate() {
    super.onCreate()
    Stetho.initializeWithDefaults(this)
  }

  override fun installLeakCanary(): RefWatcher {
    return LeakCanary.install(this)
  }
}