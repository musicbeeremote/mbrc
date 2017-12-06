package com.kelsos.mbrc.platform

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kelsos.mbrc.R
import com.kelsos.mbrc.RemoteServiceCore
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager.Companion.CHANNEL_ID
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager.Companion.NOW_PLAYING_PLACEHOLDER
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager.Companion.channel
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteService : Service() {

  private val controllerBinder = ControllerBinder()

  @Inject
  lateinit var receiver: RemoteBroadcastReceiver

  @Inject
  lateinit var core: RemoteServiceCore

  private lateinit var scope: Scope
  private lateinit var handler: Handler

  private fun placeholderNotification(): Notification {
    val channel = channel()
    channel?.let { notificationChannel ->
      val manager = NotificationManagerCompat.from(this)
      manager.createNotificationChannel(notificationChannel)
    }
    val cancelIntent = getPendingIntent(RemoteViewIntentBuilder.CANCEL, this)
    val action = NotificationCompat.Action.Builder(
      R.drawable.ic_close_black_24dp,
      getString(android.R.string.cancel),
      cancelIntent
    ).build()

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setSmallIcon(R.drawable.ic_mbrc_status)
      .setContentTitle(getString(R.string.application_name))
      .addAction(action)
      .setContentText(getString(R.string.application_starting))
      .build()
  }

  override fun onBind(intent: Intent?): IBinder = controllerBinder

  override fun onCreate() {
    super.onCreate()
    Timber.d("Background Service::Created")
    startForeground(NOW_PLAYING_PLACEHOLDER, placeholderNotification())
    handler = Handler(Looper.myLooper()!!)
    SERVICE_RUNNING = true
    scope = Toothpick.openScope(application)
    Toothpick.inject(this, scope)
    this.registerReceiver(receiver, receiver.filter(this))
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Background Service::Started")
    startForeground(NOW_PLAYING_PLACEHOLDER, placeholderNotification())
    core.start()
    core.setSyncStartAction { LibrarySyncService.startActionSync(this, true) }
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    SERVICE_STOPPING = true
    stopForeground(true)
    this.unregisterReceiver(receiver)
    handler.postDelayed(
      {
        core.stop()
        Toothpick.closeScope(this)
        SERVICE_STOPPING = false
        SERVICE_RUNNING = false
        Timber.d("Background Service::Destroyed")
      },
      150
    )
  }

  private inner class ControllerBinder : Binder() {
    val service: ControllerBinder
      @SuppressWarnings("unused")
      get() = this@ControllerBinder
  }

  companion object {
    var SERVICE_RUNNING = false
    var SERVICE_STOPPING = false
  }
}
