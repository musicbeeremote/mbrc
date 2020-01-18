package com.kelsos.mbrc.platform.mediasession

import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.platform.ForegroundHooks

interface INotificationManager {

  fun cancel(notificationId: Int = NOW_PLAYING_PLACEHOLDER)

  fun setForegroundHooks(hooks: ForegroundHooks)

  fun trackChanged(playingTrack: PlayingTrack)

  fun playerStateChanged(@PlayerState.State state: String)

  fun connectionStateChanged(connected: Boolean)

  companion object {
    const val NOW_PLAYING_PLACEHOLDER = 15613
    const val CHANNEL_ID = "mbrc_session_01"
  }
}