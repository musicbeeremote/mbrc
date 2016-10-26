package com.kelsos.mbrc.ui.mini_control

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState.State
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface MiniControlView : BaseView {
  fun updatePlayerState(@State state: String)
  fun updateTrack(artist: String, title: String)
  fun updateCover(cover: Bitmap?)
}

interface MiniControlPresenter : Presenter<MiniControlView> {
  fun onNextPressed()
  fun onPreviousPressed()
  fun onPlayPause()
  fun load()
}
