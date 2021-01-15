package com.kelsos.mbrc.platform

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kelsos.mbrc.core.IRemoteServiceCore
import com.kelsos.mbrc.features.library.sync.SyncWorkHandler
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import org.koin.android.ext.android.inject
import timber.log.Timber

class RemoteService : Service(), ForegroundHooks {

  private val receiver: RemoteBroadcastReceiver by inject()
  private val core: IRemoteServiceCore by inject()
  private val notifications: SessionNotificationManager by inject()
  private val syncWorkHandler: SyncWorkHandler by inject()

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    notifications.setForegroundHooks(this)
    this.registerReceiver(receiver, receiver.filter(this))
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Background Service::Started")
    notifications.setForegroundHooks(this)
    core.start()
    core.setSyncStartAction { syncWorkHandler.sync(true) }
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    core.stop()
    this.unregisterReceiver(receiver)
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
