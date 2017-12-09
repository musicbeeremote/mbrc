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
import com.kelsos.mbrc.events.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.CoverChangedEvent
import com.kelsos.mbrc.events.PlayStateChange
import com.kelsos.mbrc.events.TrackInfoChangeEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.platform.ForegroundHooks
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.NEXT
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.OPEN
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PLAY
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PREVIOUS
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionNotificationManager
@Inject
constructor(
    bus: RxBus,
    private val context: Application,
    private val sessionManager: RemoteSessionManager,
    private val settings: SettingsManager,
    private val model: SessionStatusModel,
    private val notificationManager: NotificationManager
) {
  private var notification: Notification? = null
  private val previous: String
  private val play: String
  private val next: String
  private var hooks: ForegroundHooks? = null

  init {
    bus.register(this, TrackInfoChangeEvent::class.java, { this.handleTrackInfo(it) })
    bus.register(this, CoverChangedEvent::class.java, { this.coverChanged(it.path) })
    bus.register(this, PlayStateChange::class.java, { this.playStateChanged(it) })
    bus.register(this, ConnectionStatusChangeEvent::class.java, { this.connectionChanged(it) })
    bus.register(this, CancelNotificationEvent::class.java, { this.cancelNotification() })
    previous = context.getString(R.string.notification_action_previous)
    play = context.getString(R.string.notification_action_play)
    next = context.getString(R.string.notification_action_next)

    createNotificationChannels()
  }

  private fun handleTrackInfo(event: TrackInfoChangeEvent) {
    model.trackInfo = event.trackInfo
    update()
  }

  private fun coverChanged(path: String) {
    val coverFile = File(path)
    if (coverFile.exists()) {
      RemoteUtils.bitmapFromFile(coverFile.absolutePath).doOnTerminate {
        update()
      }.subscribe({
        model.cover = it
      }, {
        Timber.v(it, "failed to decode")
        model.cover = null
      })
    } else {
      model.cover = null
      update()
    }
  }

  private fun playStateChanged(event: PlayStateChange) {
    model.playState = event.state
    update()
  }

  private fun update() {
    notification = createBuilder().build()
    notificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification)
  }

  private fun connectionChanged(event: ConnectionStatusChangeEvent) {
    if (!settings.isNotificationControlEnabled()) {
      Timber.v("Notification is off doing nothing")
      return
    }

    if (event.status == Connection.OFF) {
      cancelNotification(NOW_PLAYING_PLACEHOLDER)
    } else {
      notification = createBuilder().build()

      notification?.let { hooks?.start(NOW_PLAYING_PLACEHOLDER, it) }
    }
  }

  private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return
    }

    val name = context.getString(R.string.notification__session_channel_name)
    val description = context.getString(R.string.notification__session_channel_description)
    val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
    channel.apply {
      this.description = description
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
    val resId = if (model.playState == PlayerState.PLAYING) {
      R.drawable.ic_action_pause
    } else {
      R.drawable.ic_action_play
    }

    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setStyle(mediaStyle.setShowActionsInCompactView(1, 2))
        .addAction(previousAction)
        .addAction(getPlayAction(resId))
        .addAction(nextAction)

    builder.priority = NotificationCompat.PRIORITY_LOW
    builder.setOnlyAlertOnce(true)

    if (model.cover != null) {
      builder.setLargeIcon(model.cover)
    } else {
      val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_image_no_cover)
      builder.setLargeIcon(icon)
    }

    val info = model.trackInfo

    info?.let {
      builder.setContentTitle(it.title).setContentText(it.artist).setSubText(it.album)
    }

    builder.setContentIntent(getPendingIntent(OPEN, context))

    return builder
  }

  private val previousAction: Action
    get() {
      val previousIntent = getPendingIntent(PREVIOUS, context)
      return Action.Builder(R.drawable.ic_action_previous, previous, previousIntent).build()
    }

  private fun getPlayAction(playStateIcon: Int): Action {
    val playIntent = getPendingIntent(PLAY, context)

    return Action.Builder(playStateIcon, play, playIntent).build()
  }

  private val nextAction: Action
    get() {
      val nextIntent = getPendingIntent(NEXT, context)
      return Action.Builder(R.drawable.ic_action_next, next, nextIntent).build()
    }

  fun cancelNotification(notificationId: Int = NOW_PLAYING_PLACEHOLDER) {
    notificationManager.cancel(notificationId)
    hooks?.stop()
  }

  fun setForegroundHooks(hooks: ForegroundHooks) {
    this.hooks = hooks
  }

  companion object {
    const val NOW_PLAYING_PLACEHOLDER = 15613
    const val CHANNEL_ID = "mbrc_session_01"
  }

  class CancelNotificationEvent
}
