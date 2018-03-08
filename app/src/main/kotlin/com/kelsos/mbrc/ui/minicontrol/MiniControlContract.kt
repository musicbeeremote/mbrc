package com.kelsos.mbrc.ui.minicontrol

import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.TrackInfo
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface MiniControlView : BaseView {
  fun updateCover(path: String = "")

  fun updateTrackInfo(trackInfo: TrackInfo)

  fun updateState(@State state: String)
}

interface MiniControlPresenter : Presenter<MiniControlView> {
  fun load()
  fun next()
  fun previous()
  fun playPause()
}