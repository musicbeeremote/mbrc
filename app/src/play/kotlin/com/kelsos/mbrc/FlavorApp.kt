package com.kelsos.mbrc

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.anrwatchdog.ANRError
import com.kelsos.mbrc.di.bindSingletonClass
import com.kelsos.mbrc.di.module
import com.kelsos.mbrc.di.scope
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncMetricsImpl
import io.fabric.sdk.android.Fabric

open class FlavorApp : App() {
  override fun onCreate() {
    super.onCreate()
    val crashlyticsKit = Crashlytics.Builder()
      .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
      .build()
    Fabric.with(this, crashlyticsKit)

    scope(applicationContext, {
      module {
        bindSingletonClass<SyncMetrics> { SyncMetricsImpl::class }
      }
    })
  }

  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
    Crashlytics.logException(anrError)
  }
}