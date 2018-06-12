package com.kelsos.mbrc.platform.mediasession

import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel

interface INotificationManager {

  fun cancel(notificationId: Int = NOW_PLAYING_PLACEHOLDER)

  fun trackChanged(playingTrack: PlayingTrackModel)

  fun playerStateChanged(@PlayerState.State state: String)

  fun connectionStateChanged(connected: Boolean)

  companion object {
    const val NOW_PLAYING_PLACEHOLDER = 15613
  }
}
