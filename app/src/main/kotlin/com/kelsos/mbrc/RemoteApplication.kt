package com.kelsos.mbrc

import android.app.Application
import android.support.annotation.CallSuper
import com.kelsos.mbrc.di.modules.RemoteModule
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.FactoryRegistry
import toothpick.smoothie.MemberInjectorRegistry
import toothpick.smoothie.module.SmoothieApplicationModule
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class RemoteApplication : Application() {

  @CallSuper
  override fun onCreate() {
    super.onCreate()

    val configuration: Configuration
    if (BuildConfig.DEBUG) {
      configuration = Configuration.forDevelopment().disableReflection()
    } else {
      configuration = Configuration.forProduction().disableReflection()
    }

    Toothpick.setConfiguration(configuration)

    MemberInjectorRegistryLocator.setRootRegistry(MemberInjectorRegistry())
    FactoryRegistryLocator.setRootRegistry(FactoryRegistry())
    val applicationScope = Toothpick.openScope(this)
    applicationScope.installModules(SmoothieApplicationModule(this), RemoteModule())

    CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/roboto_regular.ttf").setFontAttrId(R.attr.fontPath).build())

    if (BuildConfig.DEBUG) {
      Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
          return super.createStackElementTag(element) + ":" +
              element.lineNumber +
              " [" + Thread.currentThread().name + "]"
        }
      })
    }

  }
}
