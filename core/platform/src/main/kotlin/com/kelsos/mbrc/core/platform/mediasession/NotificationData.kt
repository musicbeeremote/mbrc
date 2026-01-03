package com.kelsos.mbrc.core.platform.mediasession

import android.graphics.Bitmap
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.platform.state.PlayingTrack

data class NotificationData(
  val track: PlayingTrack = PlayingTrack(),
  val playerState: PlayerState = PlayerState.Stopped,
  val cover: Bitmap? = null,
  val isStream: Boolean = false,
  val elapsedTime: String = ""
)
