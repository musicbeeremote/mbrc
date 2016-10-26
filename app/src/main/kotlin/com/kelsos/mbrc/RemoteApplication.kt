package com.kelsos.mbrc

import android.app.Application
import android.content.Intent
import android.support.multidex.MultiDex
import com.kelsos.mbrc.controller.Controller
import com.kelsos.mbrc.di.modules.RemoteModule
import com.kelsos.mbrc.extensions.initDBFlow
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule

open class RemoteApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    MultiDex.install(this)
    this.initDBFlow()
    val configuration: Configuration
    if (BuildConfig.DEBUG) {
      configuration = Configuration.forDevelopment().disableReflection()
    } else {
      configuration = Configuration.forProduction().disableReflection()
    }

    Toothpick.setConfiguration(configuration)

    MemberInjectorRegistryLocator.setRootRegistry(com.kelsos.mbrc.MemberInjectorRegistry())
    FactoryRegistryLocator.setRootRegistry(com.kelsos.mbrc.FactoryRegistry())
    val applicationScope = Toothpick.openScope(this)
    applicationScope.installModules(SmoothieApplicationModule(this), RemoteModule())

    startService(Intent(this, Controller::class.java))

    if (BuildConfig.DEBUG) {
      Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
          return "${super.createStackElementTag(element)}:${element.lineNumber} [${Thread.currentThread().name}]"
        }
      })
    }
  }
}
