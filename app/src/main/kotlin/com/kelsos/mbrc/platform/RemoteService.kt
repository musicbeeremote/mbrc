package com.kelsos.mbrc.platform

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kelsos.mbrc.IRemoteServiceCore
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class RemoteService : Service(), ForegroundHooks {

  @Inject
  lateinit var receiver: RemoteBroadcastReceiver

  @Inject
  lateinit var core: IRemoteServiceCore

  @Inject
  lateinit var notifications: SessionNotificationManager

  private var scope: Scope? = null

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    scope = Toothpick.openScope(application)
    Toothpick.inject(this, scope)
    this.registerReceiver(receiver, receiver.filter())
    notifications.setForegroundHooks(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Background Service::Started")
    core.start()
    core.setSyncStartAction { LibrarySyncService.startActionSync(this, true) }
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

  override fun onTaskRemoved(rootIntent: Intent?) {
    super.onTaskRemoved(rootIntent)
    notifications.cancel()
  }
}