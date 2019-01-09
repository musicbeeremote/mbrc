package com.kelsos.mbrc

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.anrwatchdog.ANRError
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncMetricsImpl
import io.fabric.sdk.android.Fabric
import org.koin.dsl.module.Module

open class FlavorApp : App() {
  override fun onCreate() {
    super.onCreate()

    val crashlyticsKit = Crashlytics.Builder()
      .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
      .build()
    Fabric.with(this, crashlyticsKit)
  }

  override fun modules(): List<Module> {
    val playModule = org.koin.dsl.module.applicationContext {
      bean<SyncMetrics> { SyncMetricsImpl() }
    }
    return super.modules().plus(playModule)
  }


  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
    Crashlytics.logException(anrError)
  }
}