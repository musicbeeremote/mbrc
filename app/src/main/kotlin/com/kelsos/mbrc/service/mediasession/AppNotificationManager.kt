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
import com.kelsos.mbrc.core.platform.mediasession.NotificationData
import com.kelsos.mbrc.core.platform.state.toPlayingTrack
import kotlinx.coroutines.launch

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
  private val notificationManager: NotificationManager,
  private val notificationBuilder: NotificationBuilder,
  private val mediaSessionManager: MediaSessionManager
) : AppNotificationManager {
  private var notification: Notification? = null
  private var notificationData: NotificationData = NotificationData()

  /**
   * What the notification on screen was last built from, or null while the placeholder shows.
   * Re-posting identical content is not free: SystemUI treats every post as fresh activity and
   * moves the card up the media carousel (#385).
   */
  private var postedData: NotificationData? = null

  /**
   * Guards [notificationData], [postedData] and the post itself. Track updates arrive from
   * [MediaSessionManager.scope] while state and connection changes arrive from the state manager's
   * thread. Unguarded, a late update could post over the disconnect placeholder, or leave
   * [postedData] naming a notification that is not the one on screen, so the skip in [update]
   * would keep the wrong one up. [NotificationManager.notify] may be called from any thread, so
   * posting happens under the lock and posts land in the order the changes were made.
   */
  private val lock = Any()

  init {
    ensureChannelExists()
  }

  private fun update(change: (NotificationData) -> NotificationData) = synchronized(lock) {
    notificationData = change(notificationData)
    val mediaSession = mediaSessionManager.mediaSession ?: return@synchronized
    if (notificationData == postedData) return@synchronized
    post(notificationBuilder.createBuilder(notificationData, mediaSession).build())
    postedData = notificationData
  }

  private fun post(notification: Notification) {
    this.notification = notification
    notificationManager.notify(AppNotificationManager.MEDIA_SESSION_NOTIFICATION_ID, notification)
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
      update { it.copy(track = playingTrack.toPlayingTrack(), cover = cover) }
    }
  }

  override fun connectionStateChanged(connected: Boolean) = synchronized(lock) {
    if (connected) {
      val mediaSession = mediaSessionManager.mediaSession ?: return@synchronized
      post(notificationBuilder.createBuilder(notificationData, mediaSession).build())
      postedData = notificationData
    } else {
      // Replace the notification with a placeholder to indicate disconnection.
      // The ServiceLifecycleManager will eventually stop the service and remove it,
      // but this avoids showing stale track data during the reconnection window.
      notificationData = NotificationData()
      postedData = null
      post(notificationBuilder.createPlaceholderBuilder().build())
    }
  }

  override fun updateState(state: PlayerState, position: PlayingPosition) =
    update { it.copy(playerState = state, isStream = position.isStream) }

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
