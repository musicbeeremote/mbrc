package com.kelsos.mbrc

import com.github.anrwatchdog.ANRError

open class FlavorApp : App() {
  override fun onCreate() {
    super.onCreate()
  }

  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
  }
}