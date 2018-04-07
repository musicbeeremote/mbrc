package com.kelsos.mbrc.platform.mediasession

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.Action
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.platform.ForegroundHooks
import com.kelsos.mbrc.platform.mediasession.INotificationManager.Companion.CHANNEL_ID
import com.kelsos.mbrc.platform.mediasession.INotificationManager.Companion.NOW_PLAYING_PLACEHOLDER
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.NEXT
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.OPEN
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PLAY
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PREVIOUS
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.RemoteUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import timber.log.Timber
import javax.inject.Inject

class SessionNotificationManager
@Inject
constructor(
  private val context: Application,
  private val sessionManager: RemoteSessionManager,
  private val settings: SettingsManager,
  private val notificationManager: NotificationManager
) : INotificationManager {

  private val previous: String by lazy { context.getString(R.string.notification_action_previous) }
  private val play: String by lazy { context.getString(R.string.notification_action_play) }
  private val next: String by lazy { context.getString(R.string.notification_action_next) }

  private val channelName by lazy { context.getString(R.string.notification__session_channel_name) }
  private val channelDescription by lazy {
    context.getString(R.string.notification__session_channel_description)
  }

  private var notification: Notification? = null
  private var hooks: ForegroundHooks? = null

  private var notificationData: NotificationData = NotificationData()

  init {
    createNotificationChannels()
  }

  private fun update() {
    notification = createBuilder().build()
    notificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification)
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }

    val channel = NotificationChannel(
      CHANNEL_ID,
      channelName,
      NotificationManager.IMPORTANCE_DEFAULT
    )

    channel.apply {
      this.description = channelDescription
      enableLights(false)
      enableVibration(false)
      setSound(null, null)
    }

    notificationManager.createNotificationChannel(channel)
  }

  private fun createBuilder(): NotificationCompat.Builder {
    val mediaStyle = android.support.v4.media.app.NotificationCompat.MediaStyle()
    mediaStyle.setMediaSession(sessionManager.mediaSessionToken)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
    val resId = if (notificationData.playerState == PlayerState.PLAYING) {
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
      builder.setLargeIcon(notificationData.cover)
    } else {
      val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_image_no_cover)
      builder.setLargeIcon(icon)
    }

    val info = notificationData.trackModel

    with(info) {
      builder.setContentTitle(title)
        .setContentText(artist)
        .setSubText(album)
    }

    builder.setContentIntent(getPendingIntent(OPEN, context))

    return builder
  }

  private fun getPreviousAction(): Action {
    val previousIntent = getPendingIntent(PREVIOUS, context)
    return Action.Builder(R.drawable.ic_action_previous, previous, previousIntent).build()
  }

  private fun getPlayAction(playStateIcon: Int): Action {
    val playIntent = getPendingIntent(PLAY, context)

    return Action.Builder(playStateIcon, play, playIntent).build()
  }

  private fun getNextAction(): Action {
    val nextIntent = getPendingIntent(NEXT, context)
    return Action.Builder(R.drawable.ic_action_next, next, nextIntent).build()
  }

  override fun cancel(notificationId: Int) {
    notificationManager.cancel(notificationId)
    hooks?.stop()
  }

  override fun setForegroundHooks(hooks: ForegroundHooks) {
    this.hooks = hooks
  }

  override fun trackChanged(playingTrack: PlayingTrackModel) {
    async(CommonPool) {
      notificationData = with(playingTrack.coverUrl) {
        val cover = if (isNotEmpty()) {
          RemoteUtils.loadBitmap(this).await()
        } else {
          null
        }
        notificationData.copy(trackModel = playingTrack, cover = cover)
      }

      update()
    }
  }

  override fun connectionStateChanged(connected: Boolean) {
    if (!settings.isNotificationControlEnabled()) {
      Timber.v("Notification is off doing nothing")
      return
    }

    if (connected) {
      cancel(NOW_PLAYING_PLACEHOLDER)
    } else {
      notification = createBuilder().build().also {
        hooks?.start(NOW_PLAYING_PLACEHOLDER, it)
      }
    }
  }

  override fun playerStateChanged(state: String) {
    if (notificationData.playerState == state) {
      return
    }
    notificationData = notificationData.copy(playerState = state)
    update()
  }
}