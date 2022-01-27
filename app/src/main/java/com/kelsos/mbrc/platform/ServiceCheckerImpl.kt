package com.kelsos.mbrc.platform

import android.app.Application
import android.content.Intent

class ServiceCheckerImpl(
  private val application: Application,
) : ServiceChecker {
  override fun startServiceIfNotRunning() {
    if (RemoteService.serviceRunning) {
      return
    }
    application.startForegroundService(Intent(application, RemoteService::class.java))
  }
}
