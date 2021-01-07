package com.kelsos.mbrc

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.app.NotificationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.annotation.CallSuper
import androidx.core.content.getSystemService
import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kelsos.mbrc.common.utilities.CustomLoggingTree
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

@SuppressLint("Registered")
@KoinExperimentalAPI
open class App : MultiDexApplication() {

  @CallSuper
  override fun onCreate() {
    super.onCreate()
    initialize()
  }

  protected open fun appModules(): List<Module> {
    val androidModule = module {
      val app = this@App as Application

      single { app.resources }
      single { checkNotNull(app.getSystemService<ActivityManager>()) }
      single { checkNotNull(app.getSystemService<AudioManager>()) }
      single { checkNotNull(app.getSystemService<NotificationManager>()) }
      single { checkNotNull(app.getSystemService<WifiManager>()) }
      single { checkNotNull(app.getSystemService<ConnectivityManager>()) }
    }
    return listOf(
      appModule,
      uiModule,
      androidModule
    )
  }

  protected open fun initialize() {
    if (!testMode()) {
      AndroidThreeTen.init(this)
    }

    startKoin {
      androidContext(this@App)
      fragmentFactory()
      workManagerFactory()
      modules(appModules())
    }

    initializeTimber()

    Kotpref.init(this)
  }

  private fun initializeTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(CustomLoggingTree.create())
    }
  }

  internal open fun testMode(): Boolean = false
}
