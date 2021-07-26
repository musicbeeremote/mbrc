package com.kelsos.mbrc

import com.kelsos.mbrc.metrics.SyncMetricsImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single

open class FlavorApp : App() {
  override fun appModules(): List<Module> {
    val playModule = module {
      single<SyncMetricsImpl>() bind SyncMetricsImpl::class
    }
    return super.appModules().plus(playModule)
  }
}
