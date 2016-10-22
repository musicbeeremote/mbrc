package com.kelsos.mbrc.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import javax.inject.Inject
import roboguice.RoboGuice

class MediaButtonReceiver : BroadcastReceiver() {
  @Inject private lateinit var handler: MediaIntentHandler

  override fun onReceive(context: Context, intent: Intent) {
    RoboGuice.getInjector(context).injectMembers(this)
    handler.handleMediaIntent(intent)
  }
}
