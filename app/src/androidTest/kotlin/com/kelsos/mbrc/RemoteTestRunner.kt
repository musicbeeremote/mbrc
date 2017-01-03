package com.kelsos.mbrc

import android.app.Application
import android.content.Context
import android.os.PowerManager
import android.support.test.runner.AndroidJUnitRunner

class RemoteTestRunner : AndroidJUnitRunner() {
  private var wakeLock: PowerManager.WakeLock? = null

  override fun onStart() {
//    val app = targetContext.applicationContext
//
//    val name = RemoteTestRunner::class.java.simpleName
//    // Wake up the screen.
//    val power = app.getSystemService(POWER_SERVICE) as PowerManager
//    wakeLock = power.newWakeLock(FULL_WAKE_LOCK or ACQUIRE_CAUSES_WAKEUP or ON_AFTER_RELEASE, name)
//    wakeLock!!.acquire()

    super.onStart()
  }

  override fun onDestroy() {
    super.onDestroy()

    wakeLock!!.release()
  }

  @Throws(IllegalAccessException::class, ClassNotFoundException::class, InstantiationException::class)
  override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
    return super.newApplication(cl, MockApplication::class.java.getName(), context)
  }
}
