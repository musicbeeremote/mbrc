package com.kelsos.mbrc.views

import android.graphics.Bitmap

import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.domain.TrackInfo

interface MiniControlView : BaseView {
  fun updateCover(cover: Bitmap?)

  fun updateTrackInfo(trackInfo: TrackInfo)

  fun updateState(@State state: String)
}
