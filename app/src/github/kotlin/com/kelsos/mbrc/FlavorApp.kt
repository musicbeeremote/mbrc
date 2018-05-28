package com.kelsos.mbrc

import com.github.anrwatchdog.ANRError
import com.kelsos.mbrc.di.bindSingletonClass
import com.kelsos.mbrc.di.module
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncMetricsImpl
import io.fabric.sdk.android.Fabric

class FlavorApp : App() {
  override fun onCreate() {
    super.onCreate()
  }

  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
  }
}