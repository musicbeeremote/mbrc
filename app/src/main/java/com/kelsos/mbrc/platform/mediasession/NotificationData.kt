package com.kelsos.mbrc.platform.mediasession

import android.graphics.Bitmap
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.features.library.tracks.PlayingTrack

data class NotificationData(
  val track: PlayingTrack = PlayingTrack(),
  @PlayerState.State
  val playerState: String = PlayerState.STOPPED,
  val cover: Bitmap? = null
)