package com.kelsos.mbrc.utilities

import android.content.Intent
import android.view.KeyEvent
import javax.inject.Inject
import javax.inject.Singleton
import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.interactors.PlayerInteractor
import timber.log.Timber

@Singleton class MediaIntentHandler
@Inject constructor(private val playerInteractor: PlayerInteractor) {
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
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> perform(PlayerAction.PLAY_PLAUSE)
        KeyEvent.KEYCODE_MEDIA_PLAY -> perform(PlayerAction.PLAY)
        KeyEvent.KEYCODE_MEDIA_PAUSE -> perform(PlayerAction.PAUSE)
        KeyEvent.KEYCODE_MEDIA_STOP -> perform(PlayerAction.STOP)
        KeyEvent.KEYCODE_MEDIA_NEXT -> perform(PlayerAction.NEXT)
        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> perform(PlayerAction.PREVIOUS)
        else -> {
        }
      }


    }
    return result
  }

  private fun perform(@PlayerAction.Action action: String) {
    playerInteractor.performAction(action).subscribe({

    }) {
      Timber.v(it, "Failed to perform action")
    }
  }

  companion object {
    const val DOUBLE_CLICK_INTERVAL = 350
  }

}
