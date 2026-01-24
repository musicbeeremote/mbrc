package com.kelsos.mbrc.service.mediasession

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeFile
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.kelsos.mbrc.R
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.platform.mediasession.NotificationData
import com.kelsos.mbrc.core.platform.state.toPlayingTrack
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface AppNotificationManager {
  fun initialize()

  fun destroy()

  fun cancel(notificationId: Int = MEDIA_SESSION_NOTIFICATION_ID)

  fun updatePlayingTrack(playingTrack: TrackInfo)

  fun updateState(state: PlayerState, position: PlayingPosition)

  fun connectionStateChanged(connected: Boolean)

  fun createPlaceholder(): Notification

  companion object {
    const val MEDIA_SESSION_NOTIFICATION_ID = 15613
    const val CHANNEL_ID = "mbrc_session_01"
  }
}

class AppNotificationManagerImpl(
  private val context: Context,
  private val dispatchers: AppCoroutineDispatchers,
  private val notificationManager: NotificationManager,
  private val notificationBuilder: NotificationBuilder,
  private val mediaSessionManager: MediaSessionManager
) : AppNotificationManager {
  private var notification: Notification? = null
  private var notificationData: NotificationData = NotificationData()

  init {
    ensureChannelExists()
  }

  private suspend fun update(notificationData: NotificationData) {
    val mediaSession = mediaSessionManager.mediaSession ?: return
    notification = notificationBuilder.createBuilder(notificationData, mediaSession).build()

    withContext(dispatchers.main) {
      notificationManager.notify(AppNotificationManager.MEDIA_SESSION_NOTIFICATION_ID, notification)
    }
  }

  override fun initialize() {
    mediaSessionManager.initialize()
  }

  override fun destroy() {
    mediaSessionManager.destroy()
  }

  override fun cancel(notificationId: Int) {
    notificationManager.cancel(notificationId)
  }

  override fun updatePlayingTrack(playingTrack: TrackInfo) {
    mediaSessionManager.scope.launch {
      val coverUrl = playingTrack.coverUrl
      val cover =
        if (coverUrl.isEmpty()) {
          null
        } else {
          val uri = coverUrl.toUri()
          runCatching {
            decodeFile(
              checkNotNull(uri.path),
              BitmapFactory.Options().apply {
                inPreferredConfig = Config.RGB_565
              }
            )
          }.getOrNull()
        }
      notificationData = notificationData.copy(track = playingTrack.toPlayingTrack(), cover = cover)
      update(notificationData)
    }
  }

  override fun connectionStateChanged(connected: Boolean) {
    if (connected) {
      val mediaSession = mediaSessionManager.mediaSession ?: return
      notification = notificationBuilder.createBuilder(this.notificationData, mediaSession).build()
      notificationManager.notify(AppNotificationManager.MEDIA_SESSION_NOTIFICATION_ID, notification)
    }
    // When disconnected, don't cancel the notification here.
    // The ServiceLifecycleManager will stop the service, and stopForeground(REMOVE)
    // will handle notification removal safely.
  }

  override fun updateState(state: PlayerState, position: PlayingPosition) {
    mediaSessionManager.scope.launch {
      notificationData = notificationData.copy(
        playerState = state,
        isStream = position.isStream,
        elapsedTime = if (position.isStream) position.currentMinutes else ""
      )
      update(notificationData)
    }
  }

  override fun createPlaceholder(): Notification {
    ensureChannelExists()
    return notificationBuilder.createPlaceholderBuilder().build()
  }

  private fun ensureChannelExists() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = createChannel()
      val manager = NotificationManagerCompat.from(context)
      manager.createNotificationChannel(channel)
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createChannel(): NotificationChannel {
    val channelName = context.getString(R.string.notification__session_channel_name)
    val channelDescription = context.getString(R.string.notification__session_channel_description)

    return NotificationChannel(
      AppNotificationManager.CHANNEL_ID,
      channelName,
      NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
      description = channelDescription
      enableLights(false)
      enableVibration(false)
      setSound(null, null)
    }
  }
}
