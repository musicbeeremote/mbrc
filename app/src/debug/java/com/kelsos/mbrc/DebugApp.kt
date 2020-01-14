package com.kelsos.mbrc

import com.facebook.stetho.Stetho

class DebugApp : FlavorApp() {
  override fun onCreate() {
    super.onCreate()
    Stetho.initializeWithDefaults(this)
  }
}