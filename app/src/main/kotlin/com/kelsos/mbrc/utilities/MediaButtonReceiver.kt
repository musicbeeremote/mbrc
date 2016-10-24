package com.kelsos.mbrc.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import javax.inject.Inject
import roboguice.RoboGuice
import toothpick.Toothpick

class MediaButtonReceiver : BroadcastReceiver() {
  @Inject lateinit var handler: MediaIntentHandler

  override fun onReceive(context: Context, intent: Intent) {
    val scope = Toothpick.openScope(context.applicationContext)
    Toothpick.inject(this, scope)
    handler.handleMediaIntent(intent)
  }
}
