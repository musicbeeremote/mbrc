package com.kelsos.mbrc.models

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.domain.TrackInfo

class NotificationModel {
  var trackInfo: TrackInfo = TrackInfo()
  var cover: Bitmap? = null
  @PlayerState.State var playState: String

  init {
    playState = PlayerState.STOPPED
  }
}
