package com.kelsos.mbrc.adapters

import android.app.Application
import android.content.Intent
import android.os.Build
import com.kelsos.mbrc.core.platform.service.ServiceRestarter
import com.kelsos.mbrc.service.RemoteService

class ServiceRestarterImpl(private val application: Application) : ServiceRestarter {
  override fun restartService() {
    application.stopService(Intent(application, RemoteService::class.java))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      application.startForegroundService(Intent(application, RemoteService::class.java))
    } else {
      application.startService(Intent(application, RemoteService::class.java))
    }
  }
}
