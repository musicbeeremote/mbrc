package com.kelsos.mbrc

import com.github.anrwatchdog.ANRError
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.metrics.SyncMetricsImpl
import org.koin.core.module.Module
import org.koin.dsl.module


open class FlavorApp : App() {

  override fun appModules(): List<Module> {
    val playModule = module {
      single<SyncMetrics> { SyncMetricsImpl() }
    }
    return super.appModules().plus(playModule)
  }

  override fun onAnr(anrError: ANRError?) {
    super.onAnr(anrError)
    anrError?.run {
      FirebaseCrashlytics.getInstance().recordException(this)
    }

  }
}