package com.kelsos.mbrc.feature.widgets.glance

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kelsos.mbrc.core.platform.intents.AppLauncher
import com.kelsos.mbrc.core.platform.intents.MediaIntentActions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Action callback for play/pause button.
 */
class PlayPauseAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    context.sendBroadcast(Intent(MediaIntentActions.PLAY_PRESSED))
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
    context.sendBroadcast(Intent(MediaIntentActions.NEXT_PRESSED))
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
    context.sendBroadcast(Intent(MediaIntentActions.PREVIOUS_PRESSED))
  }
}

/**
 * Action callback to open the main app.
 * Uses AppLauncher from Koin to get the app launch intent.
 */
class OpenAppAction :
  ActionCallback,
  KoinComponent {
  private val appLauncher: AppLauncher by inject()

  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    val pendingIntent = appLauncher.getLaunchPendingIntent(context)
    pendingIntent.send()
  }
}
