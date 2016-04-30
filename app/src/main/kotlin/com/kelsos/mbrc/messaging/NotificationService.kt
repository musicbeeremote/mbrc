package com.kelsos.mbrc.messaging

import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat.Action
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.NotificationCompat
import com.google.inject.Inject
import com.google.inject.Singleton
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.models.NotificationModel
import com.kelsos.mbrc.services.RemoteSessionManager
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.NEXT
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.OPEN
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.PLAY
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.PREVIOUS
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.getPendingIntent
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import timber.log.Timber

@Singleton class
NotificationService
@Inject
constructor(private val context: Context,
            bus: RxBus,
            private val sessionManager: RemoteSessionManager,
            private val settings: SettingsManager) {
  private var notification: Notification? = null
  @Inject private val notificationManager: NotificationManagerCompat? = null
  @Inject private val model: NotificationModel? = null
  private val previous: String
  private val play: String
  private val next: String

  init {
    bus.register(this, TrackInfoChangeEvent::class.java, { this.handleTrackInfo(it) })
    bus.register(this, CoverChangedEvent::class.java, { this.coverChanged(it) })
    bus.register(this, PlayStateChange::class.java, { this.playStateChanged(it) })
    bus.register(this, ConnectionStatusChangeEvent::class.java, { this.connectionChanged(it) })
    previous = context.getString(R.string.notification_action_previous)
    play = context.getString(R.string.notification_action_play)
    next = context.getString(R.string.notification_action_next)
  }

  private fun handleTrackInfo(event: TrackInfoChangeEvent) {
    model!!.trackInfo = event.trackInfo
    notification = createBuilder().build()
    notificationManager!!.notify(NOW_PLAYING_PLACEHOLDER, notification)
  }

  private fun coverChanged(event: CoverChangedEvent) {
    model!!.cover = event.cover
    notification = createBuilder().build()
    notificationManager!!.notify(NOW_PLAYING_PLACEHOLDER, notification)
  }

  private fun playStateChanged(event: PlayStateChange) {
    model!!.playState = event.state
    notification = createBuilder().build()
    notificationManager!!.notify(NOW_PLAYING_PLACEHOLDER, notification)
  }

  private fun connectionChanged(event: ConnectionStatusChangeEvent) {
    if (!settings.isNotificationControlEnabled) {
      Timber.v("Notification is off doing nothing")
      return
    }

    if (event.status == Connection.OFF) {
      notificationManager?.cancel(NOW_PLAYING_PLACEHOLDER)
    }

    notification = createBuilder().build()
  }

  private fun createBuilder(): NotificationCompat.Builder {
    val mediaStyle = NotificationCompat.MediaStyle()
    mediaStyle.setMediaSession(sessionManager.token)

    val builder = NotificationCompat.Builder(context)
    val resId = if (model!!.playState == PlayerState.PLAYING)
      R.drawable.ic_action_pause
    else
      R.drawable.ic_action_play

    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setSmallIcon(R.drawable.ic_mbrc_status).setStyle(
        mediaStyle.setShowActionsInCompactView(1,
            2)).addAction(previousAction).addAction(getPlayAction(resId)).addAction(nextAction)

    if (model.cover != null) {
      builder.setLargeIcon(model.cover)
    } else {
      val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_image_no_cover)
      builder.setLargeIcon(icon)
    }

    val info = model.trackInfo

    if (info.title.isNotBlank()) {
      builder.setContentTitle(info.title)
          .setContentText(info.artist)
          .setSubText(info.album)
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

  fun cancelNotification(notificationId: Int) {
    notificationManager?.cancel(notificationId)
  }

  companion object {
    val NOW_PLAYING_PLACEHOLDER = 15613
  }
}
