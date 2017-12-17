package com.kelsos.mbrc

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kelsos.mbrc.di.modules.RemoteModule
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule

@SuppressLint("Registered")
open class RemoteApplication : MultiDexApplication() {

  private var refWatcher: RefWatcher? = null

  @CallSuper
  override fun onCreate() {
    super.onCreate()
    initialize()
  }

  open protected fun initialize() {
    if (!testMode()) {
      AndroidThreeTen.init(this)
    }

    initializeToothpick()
    initializeTimber()
    initializeLeakCanary()
  }

  private fun initializeLeakCanary() {
    if (testMode()){
      return
    }

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    refWatcher = installLeakCanary()
  }

  private fun initializeTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
          return "${super.createStackElementTag(element)}:${element.lineNumber} [${Thread.currentThread().name}]"
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

  open internal fun installLeakCanary(): RefWatcher {
    return RefWatcher.DISABLED
  }

  fun getRefWatcher(context: Context): RefWatcher? {
    val application = context.applicationContext as RemoteApplication
    return application.refWatcher
  }

  open internal fun testMode(): Boolean = false
}
