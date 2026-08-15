package com.kelsos.mbrc.service

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

fun interface ServiceChecker {
  fun startServiceIfNotRunning()
}

class ServiceCheckerImpl(private val application: Application) : ServiceChecker {
  override fun startServiceIfNotRunning() {
    if (ServiceState.isRunning) {
      return
    }
    val intent = Intent(application, RemoteService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Timber.v("Starting foreground service")
      startForegroundServiceSafely(intent)
    } else {
      application.startService(intent)
    }
  }

  /**
   * Asks for the service, tolerating Android 12+ refusing the request while the app is in the
   * background. [Application.startForegroundService] throws
   * `ForegroundServiceStartNotAllowedException`, an [IllegalStateException] subclass, in that case.
   *
   * #322 guarded the other half of this — [RemoteService] promoting itself once it has been
   * created — but the request that asks for the service in the first place was left unguarded, so
   * the crash came back from here (#344). Both callers can reach this from the background: the
   * connection toggle runs in a coroutine that can resume after the app has stopped, and an
   * activity being created is not necessarily an activity that stays in the foreground.
   *
   * Not getting the service is the correct outcome when the system refuses; it is started again
   * the next time the user opens the app.
   */
  @RequiresApi(Build.VERSION_CODES.O)
  private fun startForegroundServiceSafely(intent: Intent) {
    try {
      application.startForegroundService(intent)
    } catch (e: IllegalStateException) {
      Timber.w(e, "startForegroundService rejected (background start on Android 12+)")
    }
  }
}
