package com.kelsos.mbrc.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.inject.Inject
import roboguice.RoboGuice

class MediaButtonReceiver : BroadcastReceiver() {
  @Inject private val handler: MediaIntentHandler? = null

  override fun onReceive(context: Context, intent: Intent) {
    if (handler == null) {
      RoboGuice.getInjector(context).injectMembers(this)
    }

    handler!!.handleMediaIntent(intent)
  }
}
