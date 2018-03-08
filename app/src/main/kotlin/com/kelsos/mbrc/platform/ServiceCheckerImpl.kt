package com.kelsos.mbrc.platform

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import javax.inject.Inject

class ServiceCheckerImpl
@Inject constructor(
  private val manager: ActivityManager,
  private val application: Application
) : ServiceChecker {

  override fun startServiceIfNotRunning() {
    if (!isMyServiceRunning(RemoteService::class.java)) {
      application.startService(Intent(application, RemoteService::class.java))
    }
  }

  private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
    return manager.getRunningServices(Integer.MAX_VALUE).any { serviceClass.name == it.service.className }
  }
}