package com.kelsos.mbrc

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.anrwatchdog.ANRError
import io.fabric.sdk.android.Fabric

class PlayApp : App() {
  override fun onCreate() {
    super.onCreate()
    val crashlyticsKit = Crashlytics.Builder()
      .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
      .build()
    Fabric.with(this, crashlyticsKit)
  }

  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
    Crashlytics.logException(anrError)
  }
}