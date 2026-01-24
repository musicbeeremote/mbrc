package com.kelsos.mbrc

import android.app.ActivityManager
import android.app.Application
import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.res.Resources
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.kelsos.mbrc.adapters.RemoteViewIntentBuilderImpl
import com.kelsos.mbrc.core.platform.intents.AppLauncher
import com.kelsos.mbrc.core.platform.intents.MediaIntentBuilder
import com.kelsos.mbrc.service.NotificationActionReceiver
import com.kelsos.mbrc.service.ServiceChecker
import com.kelsos.mbrc.service.ServiceCheckerImpl
import com.kelsos.mbrc.service.ServiceLifecycleManager
import com.kelsos.mbrc.service.ServiceLifecycleManagerImpl
import com.kelsos.mbrc.service.mediasession.AppNotificationManager
import com.kelsos.mbrc.service.mediasession.AppNotificationManagerImpl
import com.kelsos.mbrc.service.mediasession.MediaIntentHandler
import com.kelsos.mbrc.service.mediasession.MediaSessionManager
import com.kelsos.mbrc.service.mediasession.NotificationBuilder
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * This module provides Android platform dependencies that require
 * Android context or system services:
 * - Android system services (NotificationManager, WorkManager, AudioManager, etc.)
 * - Media session and notification components
 * - Service management components
 * - SharedPreferences
 */
val androidModule = module {
  // Android System Services
  single<Resources> { get<Application>().resources }
  single<ActivityManager> { checkNotNull(get<Application>().getSystemService()) }
  single<AudioManager> { checkNotNull(get<Application>().getSystemService()) }
  single<NotificationManager> { checkNotNull(get<Application>().getSystemService()) }
  single<WifiManager> { checkNotNull(get<Application>().getSystemService()) }
  single<ConnectivityManager> { checkNotNull(get<Application>().getSystemService()) }
  single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
  single<NotificationManagerCompat> { NotificationManagerCompat.from(get()) }
  single { WorkManager.getInstance(get()) }

  // Service Components
  singleOf(::NotificationActionReceiver)
  singleOf(::ServiceCheckerImpl) { bind<ServiceChecker>() }
  singleOf(::ServiceLifecycleManagerImpl) { bind<ServiceLifecycleManager>() }

  // Media Session and Notifications
  singleOf(::MediaSessionManager)
  singleOf(::NotificationBuilder)
  singleOf(::MediaIntentHandler)
  singleOf(::RemoteViewIntentBuilderImpl) {
    bind<MediaIntentBuilder>()
    bind<AppLauncher>()
  }
  singleOf(::AppNotificationManagerImpl) { bind<AppNotificationManager>() }
}
