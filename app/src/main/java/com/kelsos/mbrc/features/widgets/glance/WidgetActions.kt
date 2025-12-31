package com.kelsos.mbrc.features.widgets.glance

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kelsos.mbrc.app.MainActivity
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder

/**
 * Action callback for play/pause button.
 */
class PlayPauseAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    context.sendBroadcast(Intent(RemoteViewIntentBuilder.PLAY_PRESSED))
  }
}

/**
 * Action callback for next track button.
 */
class NextTrackAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    context.sendBroadcast(Intent(RemoteViewIntentBuilder.NEXT_PRESSED))
  }
}

/**
 * Action callback for previous track button.
 */
class PreviousTrackAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    context.sendBroadcast(Intent(RemoteViewIntentBuilder.PREVIOUS_PRESSED))
  }
}

/**
 * Action callback to open the main app.
 */
class OpenAppAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    context.startActivity(intent)
  }
}
