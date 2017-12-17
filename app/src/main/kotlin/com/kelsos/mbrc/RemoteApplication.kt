package com.kelsos.mbrc

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kelsos.mbrc.di.modules.RemoteModule
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule

@SuppressLint("Registered")
open class RemoteApplication : MultiDexApplication() {

  @CallSuper
  override fun onCreate() {
    super.onCreate()
    initialize()
  }

  protected open fun initialize() {
    if (!testMode()) {
      AndroidThreeTen.init(this)
    }

    initializeToothpick()
    initializeTimber()
  }

  private fun initializeTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
          val threadName = Thread.currentThread().name
          return "${super.createStackElementTag(element)}:${element.lineNumber} [$threadName]"
        }
      })
    }
  }

  private fun initializeToothpick() {
    val configuration: Configuration = if (BuildConfig.DEBUG) {
      Configuration.forDevelopment().disableReflection()
    } else {
      Configuration.forProduction().disableReflection()
    }

    Toothpick.setConfiguration(configuration)

    MemberInjectorRegistryLocator.setRootRegistry(MemberInjectorRegistry())
    FactoryRegistryLocator.setRootRegistry(FactoryRegistry())
    val applicationScope = Toothpick.openScope(this)
    if (!testMode()) {
      applicationScope.installModules(SmoothieApplicationModule(this), RemoteModule())
    }
  }

  internal open fun testMode(): Boolean = false
}
