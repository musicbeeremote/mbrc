package com.kelsos.mbrc.platform.mediasession

import android.graphics.Bitmap
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel

data class NotificationData(
  val trackModel: PlayingTrackModel = PlayingTrackModel(),
  @PlayerState.State
  val playerState: String = PlayerState.STOPPED,
  val cover: Bitmap? = null
)