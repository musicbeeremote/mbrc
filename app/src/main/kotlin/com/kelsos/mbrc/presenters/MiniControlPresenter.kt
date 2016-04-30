package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.MiniControlView

interface MiniControlPresenter {
  fun onNextPressed()
  fun onPreviousPressed()
  fun onPlayPause()
  fun bind(view: MiniControlView)
  fun onResume()
  fun onPause()
  fun load()
}
