package com.kelsos.mbrc

import android.content.Context
import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication
import com.kelsos.mbrc.di.modules.RemoteModule
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

open class RemoteApplication : MultiDexApplication() {

  private lateinit var refWatcher: RefWatcher

  @CallSuper
  override fun onCreate() {
    super.onCreate()
    FlowManager.init(FlowConfig.Builder(this).openDatabasesOnInit(true).build())
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

    CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/roboto_regular.ttf")
        .setFontAttrId(R.attr.fontPath)
        .build())

    if (BuildConfig.DEBUG) {
      Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
          return "${super.createStackElementTag(element)}:${element.lineNumber} [${Thread.currentThread().name}]"
        }
      })
    }

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    refWatcher = installLeakCanary()
  }

  internal open fun installLeakCanary(): RefWatcher {
    return RefWatcher.DISABLED
  }

  fun getRefWatcher(context: Context): RefWatcher {
    val application = context.applicationContext as RemoteApplication
    return application.refWatcher
  }
}
