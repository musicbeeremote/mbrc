package com.kelsos.mbrc.ui.minicontrol

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface MiniControlView : BaseView {
  fun updateTrackInfo(track: PlayingTrack)

  fun updateStatus(status: PlayerStatusModel)
}

interface MiniControlPresenter : Presenter<MiniControlView> {
  fun next()
  fun previous()
  fun playPause()
}