package com.kelsos.mbrc.core.platform.intents

import android.app.PendingIntent
import android.content.Context

/**
 * Abstraction for launching the main app activity.
 * Implemented by the app module to provide the MainActivity intent.
 */
interface AppLauncher {
  fun getLaunchPendingIntent(context: Context): PendingIntent
}
