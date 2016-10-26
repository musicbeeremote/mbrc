package com.kelsos.mbrc.views

import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.mvp.BaseView

interface MiniControlView : BaseView {
  fun updateCover(cover: String)

  fun updateTrackInfo(trackInfo: TrackInfo)

  fun updateState(@State state: String)
}
