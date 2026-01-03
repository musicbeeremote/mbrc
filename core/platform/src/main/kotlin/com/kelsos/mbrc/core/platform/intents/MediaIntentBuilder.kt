package com.kelsos.mbrc.core.platform.intents

import android.app.PendingIntent
import android.content.Context
import com.kelsos.mbrc.core.platform.mediasession.RemoteIntentCode

/**
 * Abstraction for building media control intents.
 * Implemented by the app module to provide app-specific intent handling.
 */
interface MediaIntentBuilder {
  fun getPendingIntent(remoteIntentCode: RemoteIntentCode, context: Context): PendingIntent
}
