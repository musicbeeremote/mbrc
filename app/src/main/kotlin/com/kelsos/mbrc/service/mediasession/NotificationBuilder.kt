package com.kelsos.mbrc.service.mediasession

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Action
import androidx.core.content.ContextCompat.getString
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import com.kelsos.mbrc.R
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.platform.intents.MediaIntentBuilder
import com.kelsos.mbrc.core.platform.mediasession.NotificationData
import com.kelsos.mbrc.core.platform.mediasession.RemoteIntentCode
import com.kelsos.mbrc.core.ui.R as CoreUiR

/**
 * Builds notifications for the media session.
 */
class NotificationBuilder(
  private val context: Context,
  private val intentBuilder: MediaIntentBuilder
) {
  /**
   * Creates a notification builder for the given notification data and media session.
   *
   * @param notificationData The data to use for the notification.
   * @param mediaSession The media session to use for the notification.
   * @return A configured notification builder.
   */
  @OptIn(UnstableApi::class)
  fun createBuilder(
    notificationData: NotificationData,
    mediaSession: MediaSession
  ): NotificationCompat.Builder {
    val style =
      MediaStyleNotificationHelper
        .MediaStyle(mediaSession)
        .setShowActionsInCompactView(0, 1, 2)
    val builder = NotificationCompat.Builder(context, AppNotificationManager.CHANNEL_ID)

    builder
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setSmallIcon(R.drawable.ic_mbrc_status)
      .setStyle(style)

    builder.priority = NotificationCompat.PRIORITY_LOW
    builder.setOnlyAlertOnce(true)

    if (notificationData.cover != null) {
      builder.setLargeIcon(notificationData.cover)
    } else {
      val icon = BitmapFactory.decodeResource(context.resources, CoreUiR.drawable.ic_image_no_cover)
      builder.setLargeIcon(icon)
    }

    with(notificationData.track) {
      builder
        .setContentTitle(title)
        .setContentText(artist)

      // For streams, show elapsed time in subtext; otherwise show album
      if (notificationData.isStream && notificationData.elapsedTime.isNotEmpty()) {
        val liveLabel = getString(context, R.string.notification__live)
        builder.setSubText("$liveLabel • ${notificationData.elapsedTime}")
      } else {
        builder.setSubText(album)
      }
    }

    builder.setContentIntent(intentBuilder.getPendingIntent(RemoteIntentCode.Open, context))

    addMediaActions(builder, notificationData.playerState)

    return builder
  }

  /**
   * Creates a placeholder notification builder.
   *
   * @return A configured notification builder for a placeholder notification.
   */
  fun createPlaceholderBuilder(): NotificationCompat.Builder {
    val builder =
      NotificationCompat
        .Builder(context, AppNotificationManager.CHANNEL_ID)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setContentTitle(getString(context, R.string.application_name))
        .setContentText(getString(context, R.string.application_starting))
        .addAction(createCancelAction())

    return builder
  }

  private fun addMediaActions(builder: NotificationCompat.Builder, playerState: PlayerState) {
    val previousAction =
      Action
        .Builder(
          R.drawable.baseline_skip_previous_24,
          context.getString(R.string.action_previous),
          intentBuilder.getPendingIntent(RemoteIntentCode.Previous, context)
        ).build()
    builder.addAction(previousAction)

    val playPauseIcon =
      if (playerState == PlayerState.Playing) {
        R.drawable.baseline_pause_24
      } else {
        R.drawable.baseline_play_arrow_24
      }
    val playPauseText =
      if (playerState == PlayerState.Playing) {
        context.getString(R.string.action_pause)
      } else {
        context.getString(R.string.action_play)
      }
    val playPauseAction =
      Action
        .Builder(
          playPauseIcon,
          playPauseText,
          intentBuilder.getPendingIntent(RemoteIntentCode.Play, context)
        ).build()
    builder.addAction(playPauseAction)

    val nextAction =
      Action
        .Builder(
          R.drawable.baseline_skip_next_24,
          context.getString(R.string.action_next),
          intentBuilder.getPendingIntent(RemoteIntentCode.Next, context)
        ).build()
    builder.addAction(nextAction)
  }

  private fun createCancelAction(): Action {
    val cancelIntent = intentBuilder.getPendingIntent(RemoteIntentCode.Cancel, context)
    return Action
      .Builder(
        R.drawable.baseline_close_24,
        getString(context, android.R.string.cancel),
        cancelIntent
      ).build()
  }
}
