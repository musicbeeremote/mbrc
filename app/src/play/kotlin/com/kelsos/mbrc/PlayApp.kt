package com.kelsos.mbrc

import com.crashlytics.android.Crashlytics
import com.github.anrwatchdog.ANRError
import com.kelsos.mbrc.BuildConfig.USE_CRASHLYTICS
import io.fabric.sdk.android.Fabric

class PlayApp : App() {
  override fun onCreate() {
    super.onCreate()
    if (USE_CRASHLYTICS) {
      Fabric.with(this, Crashlytics())
    }
  }

  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
    Crashlytics.logException(anrError)
  }
}