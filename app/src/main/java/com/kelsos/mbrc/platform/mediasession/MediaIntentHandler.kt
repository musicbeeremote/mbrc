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

  init {
    previousClick = 0
  }

  fun handleMediaIntent(mediaIntent: Intent?): Boolean {
    var result = false
    val intent = mediaIntent ?: return false
    val action = intent.action

    if (action == Intent.ACTION_MEDIA_BUTTON) {
      val extras = intent.extras ?: return false

      val event = extras.get(Intent.EXTRA_KEY_EVENT) as KeyEvent? ?: return false

      if (event.action != KeyEvent.ACTION_DOWN) {
        return false
      }

      result = when (event.keyCode) {
        KeyEvent.KEYCODE_HEADSETHOOK -> {
          val currentClick = System.currentTimeMillis()
          if (currentClick - previousClick < DOUBLE_CLICK_INTERVAL) {
            return postAction(UserAction(Protocol.PlayerNext, true))
          }
          previousClick = currentClick
          postAction(UserAction(Protocol.PlayerPlayPause, true))
        }
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
    return result
  }

  private fun postAction(action: UserAction): Boolean {
    userActionUseCase.tryPerform(action)
    return true
  }

  companion object {
    private const val DOUBLE_CLICK_INTERVAL = 350
  }
}
