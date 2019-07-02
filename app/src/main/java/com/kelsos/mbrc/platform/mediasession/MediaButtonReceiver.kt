package com.kelsos.mbrc.platform.mediasession

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent

class MediaButtonReceiver : BroadcastReceiver(), KoinComponent {

  override fun onReceive(context: Context, intent: Intent) {
    getKoin().get<MediaIntentHandler>().handleMediaIntent(intent)
  }
}