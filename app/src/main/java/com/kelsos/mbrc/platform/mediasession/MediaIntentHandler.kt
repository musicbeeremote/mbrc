package com.kelsos.mbrc.platform.mediasession

import android.content.Intent
import android.view.KeyEvent
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol

class MediaIntentHandler(
  private val userActionUseCase: UserActionUseCase
) {
  private var previousClick: Long = 0

  private fun getKeyEventFromIntent(mediaIntent: Intent?): KeyEvent? {
    val action = mediaIntent?.action

    if (action == Intent.ACTION_MEDIA_BUTTON) {
      val extras = mediaIntent.extras
      return extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent?
    }
    return null
  }

  private fun detectDoubleClick(): Boolean {
    val currentClick = System.currentTimeMillis()
    if (currentClick - previousClick < DOUBLE_CLICK_INTERVAL) {
      return postAction(UserAction(Protocol.PlayerNext, true))
    }
    previousClick = currentClick
    return postAction(UserAction(Protocol.PlayerPlayPause, true))
  }

  fun handleMediaIntent(mediaIntent: Intent?): Boolean {
    val event = getKeyEventFromIntent(mediaIntent)
    if (event?.action != KeyEvent.ACTION_DOWN) {
      return false
    }

    return when (event.keyCode) {
      KeyEvent.KEYCODE_HEADSETHOOK -> detectDoubleClick()
      KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> postAction(UserAction(Protocol.PlayerPlayPause, true))
      KeyEvent.KEYCODE_MEDIA_PLAY -> postAction(UserAction(Protocol.PlayerPlay, true))
      KeyEvent.KEYCODE_MEDIA_PAUSE -> postAction(UserAction(Protocol.PlayerPause, true))
      KeyEvent.KEYCODE_MEDIA_STOP -> postAction(UserAction(Protocol.PlayerStop, true))
      KeyEvent.KEYCODE_MEDIA_NEXT -> postAction(UserAction(Protocol.PlayerNext, true))
      KeyEvent.KEYCODE_MEDIA_PREVIOUS ->
        postAction(UserAction(Protocol.PlayerPrevious, true))
      else -> false
    }
  }

  private fun postAction(action: UserAction): Boolean {
    userActionUseCase.tryPerform(action)
    return true
  }

  companion object {
    private const val DOUBLE_CLICK_INTERVAL = 350
  }
}
