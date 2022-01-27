package com.kelsos.mbrc.platform.mediasession

import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.Duration
import com.kelsos.mbrc.features.library.PlayingTrack

interface INotificationManager {
  fun cancel(notificationId: Int = NOW_PLAYING_PLACEHOLDER)

  fun updatePlayingTrack(playingTrack: PlayingTrack)

  fun updateState(
    state: PlayerState,
    current: Duration,
  )

  fun connectionStateChanged(connected: Boolean)

  companion object {
    const val NOW_PLAYING_PLACEHOLDER = 15613
  }
}
