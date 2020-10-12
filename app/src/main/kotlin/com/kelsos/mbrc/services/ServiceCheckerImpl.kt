package com.kelsos.mbrc.services

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.os.Build
import com.kelsos.mbrc.controller.RemoteService
import timber.log.Timber
import javax.inject.Inject

class ServiceCheckerImpl
@Inject constructor(
  private val manager: ActivityManager,
  private val application: Application
) : ServiceChecker {

  override fun startServiceIfNotRunning() {
    if (!isMyServiceRunning(RemoteService::class.java)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Timber.v("Starting foreground service")
        application.startForegroundService(Intent(application, RemoteService::class.java))
      } else {
        application.startService(Intent(application, RemoteService::class.java))
      }

    }
  }

  private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
    return manager.getRunningServices(Integer.MAX_VALUE)
      .any { serviceClass.name == it.service.className }
  }
}
