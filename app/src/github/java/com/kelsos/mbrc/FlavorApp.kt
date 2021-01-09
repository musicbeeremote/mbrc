package com.kelsos.mbrc

import com.kelsos.mbrc.metrics.DummySyncMetrics
import com.kelsos.mbrc.metrics.SyncMetrics
import org.koin.core.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module

@KoinExperimentalAPI
open class FlavorApp : App() {

  override fun appModules(): List<Module> {
    return super.appModules().plus(
      module {
        single<SyncMetrics> { DummySyncMetrics() }
      }
    )
  }
}
