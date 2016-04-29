package com.kelsos.mbrc.utilities

import android.content.Intent
import android.view.KeyEvent
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton class MediaIntentHandler
@Inject constructor(private val bus: RxBus) {
  private var previousClick: Long = 0

  init {
    previousClick = 0
  }

  fun handleMediaIntent(mediaIntent: Intent): Boolean {
    val result = false

    //noinspection StatementWithEmptyBody
    if (mediaIntent.action == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
      // Handle somehow
    } else if (mediaIntent.action == Intent.ACTION_MEDIA_BUTTON) {
      val extras = mediaIntent.extras
      val keyEvent = extras.get(Intent.EXTRA_KEY_EVENT) as KeyEvent?

      if (keyEvent?.action != KeyEvent.ACTION_DOWN) {
        return false
      }

      when (keyEvent?.keyCode) {
        KeyEvent.KEYCODE_HEADSETHOOK -> {
          val currentClick = System.currentTimeMillis()
          if (currentClick - previousClick < DOUBLE_CLICK_INTERVAL) {
            return false
          }
          previousClick = currentClick
        }
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
        }
        KeyEvent.KEYCODE_MEDIA_PLAY -> {
        }
        KeyEvent.KEYCODE_MEDIA_PAUSE -> {
        }
        KeyEvent.KEYCODE_MEDIA_STOP -> {
        }
        KeyEvent.KEYCODE_MEDIA_NEXT -> {
        }
        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
        }
        else -> {
        }
      }
    }
    return result
  }

  companion object {

    const val DOUBLE_CLICK_INTERVAL = 350
  }

}
