package com.kelsos.mbrc.platform.mediasession

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

@KoinApiExtension
class MediaButtonReceiver : BroadcastReceiver(), KoinComponent {

  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action
    if (
      action != Intent.ACTION_MEDIA_BUTTON &&
      action != AudioManager.ACTION_AUDIO_BECOMING_NOISY
    ) {
      return
    }
    getKoin().get<MediaIntentHandler>().handleMediaIntent(intent)
  }
}
