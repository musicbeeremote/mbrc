package com.kelsos.mbrc.platform.mediasession

import android.graphics.Bitmap
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.features.library.PlayingTrack

data class NotificationData(
  val track: PlayingTrack = PlayingTrack(),
  val playerState: PlayerState = PlayerState.Stopped,
  val cover: Bitmap? = null
)
