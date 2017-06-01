package com.kelsos.mbrc.platform

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kelsos.mbrc.RemoteServiceCore
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteService : Service(), ForegroundHooks {

  private val controllerBinder = ControllerBinder()

  @Inject lateinit var receiver: RemoteBroadcastReceiver
  @Inject lateinit var core: RemoteServiceCore

  private var scope: Scope? = null

  override fun onBind(intent: Intent?): IBinder = controllerBinder

  override fun onCreate() {
    super.onCreate()
    scope = Toothpick.openScope(application)
    Toothpick.inject(this, scope)
    this.registerReceiver(receiver, receiver.filter())
    core.attach(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Background Service::Started")
    core.start()
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    core.stop()
    this.unregisterReceiver(receiver)
    Toothpick.closeScope(this)
  }

  override fun start(id: Int, notification: Notification) {
    Timber.v("Notification is starting foreground")
    startForeground(id, notification)
  }

  override fun stop() {
    Timber.v("Notification is stopping foreground")
    stopForeground(true)
  }

  private inner class ControllerBinder : Binder() {
    internal val service: ControllerBinder
      @SuppressWarnings("unused")
      get() = this@ControllerBinder
  }
}
