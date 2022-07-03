package com.kelsos.mbrc.platform

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.AppStateManager
import com.kelsos.mbrc.platform.mediasession.RemoteIntentCode
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager.Companion.CHANNEL_ID
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager.Companion.NOW_PLAYING_PLACEHOLDER
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager.Companion.channel
import org.koin.android.ext.android.inject
import timber.log.Timber

class RemoteService : Service() {

  private val receiver: RemoteBroadcastReceiver by inject()
  private val appStateManager: AppStateManager by inject()
  private lateinit var handler: Handler

  private fun placeholderNotification(): Notification {
    val channel = channel(this)
    channel?.let { notificationChannel ->
      val manager = NotificationManagerCompat.from(this)
      manager.createNotificationChannel(notificationChannel)
    }
    val cancelIntent = getPendingIntent(RemoteIntentCode.Cancel, this)
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

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    Timber.d("Background Service::Created")
    startForeground(NOW_PLAYING_PLACEHOLDER, placeholderNotification())
    handler = Handler(Looper.myLooper()!!)
    SERVICE_RUNNING = true
    this.registerReceiver(receiver, receiver.filter(this))
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Background Service::Started")
    startForeground(NOW_PLAYING_PLACEHOLDER, placeholderNotification())
    appStateManager.setStateHandler { playing ->
      if (playing) {
        startForeground(NOW_PLAYING_PLACEHOLDER, placeholderNotification())
      } else {
        stopForeground(STOP_FOREGROUND_REMOVE)
      }
    }
    appStateManager.start()
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    appStateManager.stop()
    SERVICE_STOPPING = true
    stopForeground(STOP_FOREGROUND_REMOVE)
    this.unregisterReceiver(receiver)
    handler.postDelayed(
      {
        SERVICE_STOPPING = false
        SERVICE_RUNNING = false
        Timber.d("Background Service::Destroyed")
      },
      DESTROY_DELAY_MS
    )
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    super.onTaskRemoved(rootIntent)
    appStateManager.stop()
  }

  companion object {
    var SERVICE_RUNNING = false
    var SERVICE_STOPPING = false
    const val DESTROY_DELAY_MS = 150L
  }
}
