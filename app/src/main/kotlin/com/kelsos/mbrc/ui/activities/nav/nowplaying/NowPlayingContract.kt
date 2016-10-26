package com.kelsos.mbrc.ui.activities.nav.nowplaying

import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.mvp.BaseView

interface NowPlayingView : BaseView {
  fun refreshingDone()
  fun reload()
  fun trackChanged(trackInfo: TrackInfo)
}

interface NowPlayingPresenter : Presenter<NowPlayingView> {
  fun refresh()
  fun play(position: Int)
  fun moveTrack(from: Int, to: Int)
  fun removeTrack(position: Int)
}
