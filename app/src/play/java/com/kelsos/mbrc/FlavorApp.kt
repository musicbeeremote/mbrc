package com.kelsos.mbrc

import com.kelsos.mbrc.metrics.SyncMetricsImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

open class FlavorApp : App() {
  override fun appModules(): List<Module> {
    val playModule =
      module {
        singleOf(::SyncMetricsImpl) { bind<SyncMetricsImpl>() }
      }
    return super.appModules().plus(playModule)
  }
}
