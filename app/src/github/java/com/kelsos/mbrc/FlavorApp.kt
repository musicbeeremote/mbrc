package com.kelsos.mbrc

import com.kelsos.mbrc.metrics.DummySyncMetrics
import com.kelsos.mbrc.metrics.SyncMetrics
import org.koin.core.module.Module
import org.koin.dsl.module

open class FlavorApp : App() {

  override fun modules(): List<Module> {
    return super.modules().plus(module {
      single<SyncMetrics> { DummySyncMetrics() }
    })
  }
}