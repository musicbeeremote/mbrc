package com.kelsos.mbrc.platform.mediasession

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class MediaButtonReceiver : BroadcastReceiver(), KoinComponent {

  private val handler: MediaIntentHandler by inject()

  override fun onReceive(context: Context, intent: Intent) {
    handler.handleMediaIntent(intent)
  }
}
