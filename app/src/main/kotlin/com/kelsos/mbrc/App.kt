package com.kelsos.mbrc

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.CallSuper
import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.github.anrwatchdog.ANRError
import com.github.anrwatchdog.ANRWatchDog
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kelsos.mbrc.di.modules.AppModule
import com.kelsos.mbrc.utilities.CustomLoggingTree
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule



@SuppressLint("Registered")
open class App : MultiDexApplication() {

  private var refWatcher: RefWatcher? = null

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
    initializeLeakCanary()
    ANRWatchDog()
      .setANRListener { onAnr(it) }
      .setIgnoreDebugger(true)
      .start()
    
    Kotpref.init(this)
  }

  protected open fun onAnr(anrError: ANRError?) {
    Timber.v(anrError, "ANR error")
  }

  private fun initializeLeakCanary() {
    if (testMode()) {
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
      Timber.plant(CustomLoggingTree.create())
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
      applicationScope.installModules(SmoothieApplicationModule(this), AppModule())
    }
  }

  internal open fun installLeakCanary(): RefWatcher {
    return RefWatcher.DISABLED
  }

  fun getRefWatcher(context: Context): RefWatcher? {
    val application = context.applicationContext as App
    return application.refWatcher
  }

  internal open fun testMode(): Boolean = false
}