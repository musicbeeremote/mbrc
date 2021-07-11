package com.kelsos.mbrc.platform.mediasession

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import androidx.core.net.toUri
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.Duration
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SessionNotificationManager(
  private val context: Application,
  private val sessionManager: RemoteSessionManager,
  private val dispatchers: AppCoroutineDispatchers,
  private val notificationManager: NotificationManager
) : INotificationManager {

  private val sessionJob: Job = Job()
  private val uiScope: CoroutineScope = CoroutineScope(dispatchers.main + sessionJob)
  private val diskScope: CoroutineScope = CoroutineScope(dispatchers.io + sessionJob)

  private val previous: String by lazy { context.getString(R.string.notification_action_previous) }
  private val play: String by lazy { context.getString(R.string.notification_action_play) }
  private val next: String by lazy { context.getString(R.string.notification_action_next) }

  private var notification: Notification? = null
  private var notificationData: NotificationData = NotificationData()

  init {
    createNotificationChannels()
  }

  private suspend fun update(notificationData: NotificationData) {
    notification = createBuilder(notificationData).build()

    withContext(dispatchers.main) {
      notificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification)
    }
  }

  private fun createNotificationChannels() {
    val channel = channel(context)
    if (channel === null) {
      return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun createBuilder(notificationData: NotificationData): NotificationCompat.Builder {
    val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
    mediaStyle.setMediaSession(sessionManager.mediaSessionToken)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
    val resId = if (notificationData.playerState == PlayerState.Playing) {
      R.drawable.ic_action_pause
    } else {
      R.drawable.ic_action_play
    }

    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setSmallIcon(R.drawable.ic_mbrc_status)
      .setStyle(mediaStyle.setShowActionsInCompactView(1, 2))
      .addAction(getPreviousAction())
      .addAction(getPlayAction(resId))
      .addAction(getNextAction())

    builder.priority = NotificationCompat.PRIORITY_LOW
    builder.setOnlyAlertOnce(true)

    if (notificationData.cover != null) {
      builder.setLargeIcon(this.notificationData.cover)
    } else {
      val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_image_no_cover)
      builder.setLargeIcon(icon)
    }

    with(notificationData.track) {
      builder.setContentTitle(title)
        .setContentText(artist)
        .setSubText(album)
    }

    builder.setContentIntent(getPendingIntent(RemoteIntentCode.Open, context))

    return builder
  }

  private fun getPreviousAction(): Action {
    val previousIntent = getPendingIntent(RemoteIntentCode.Previous, context)
    return Action.Builder(R.drawable.ic_action_previous, previous, previousIntent).build()
  }

  private fun getPlayAction(playStateIcon: Int): Action {
    val playIntent = getPendingIntent(RemoteIntentCode.Play, context)

    return Action.Builder(playStateIcon, play, playIntent).build()
  }

  private fun getNextAction(): Action {
    val nextIntent = getPendingIntent(RemoteIntentCode.Next, context)
    return Action.Builder(R.drawable.ic_action_next, next, nextIntent).build()
  }

  override fun cancel(notificationId: Int) {
    notificationManager.cancel(notificationId)
  }

  override fun updatePlayingTrack(playingTrack: PlayingTrack) {
    diskScope.launch {
      val coverUrl = playingTrack.coverUrl
      val cover = if (coverUrl.isEmpty()) {
        null
      } else {
        val uri = coverUrl.toUri()
        RemoteUtils.loadBitmap(checkNotNull(uri.path)).orNull()
      }
      notificationData = notificationData.copy(track = playingTrack, cover = cover)
      update(notificationData)
      sessionManager.updateTrack(playingTrack, cover)
    }
  }

  override fun connectionStateChanged(connected: Boolean) {
    if (connected) {
      cancel(NOW_PLAYING_PLACEHOLDER)
    } else {
      notification = createBuilder(this.notificationData).build()
    }
    sessionManager.updateConnection(connected)
  }

  override fun updateState(state: PlayerState, current: Duration) {
    uiScope.launch {
      notificationData = notificationData.copy(playerState = state)
      update(notificationData)
      sessionManager.updateState(state, current)
    }
  }

  companion object {
    const val NOW_PLAYING_PLACEHOLDER = 15613
    const val CHANNEL_ID = "mbrc_session_01"

    fun channel(context: Context): NotificationChannel? {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return null
      }

      val channelName = context.getString(R.string.notification__session_channel_name)
      val channelDescription = context.getString(R.string.notification__session_channel_description)

      val channel = NotificationChannel(
        CHANNEL_ID,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
      )

      return channel.apply {
        this.description = channelDescription
        enableLights(false)
        enableVibration(false)
        setSound(null, null)
      }
    }
  }
}
