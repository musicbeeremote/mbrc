package com.kelsos.mbrc.platform.mediasession

import android.graphics.Bitmap

import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import javax.inject.Inject

class SessionStatusModel
@Inject
constructor() {
  var trackInfo: TrackInfo? = null
  var cover: Bitmap? = null
  @State
  var playState: String = PlayerState.STOPPED
}
