package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.MainView

interface MainViewPresenter {
  fun bind(mainView: MainView)

  fun onPause()

  fun onResume()

  fun onPlayPausePressed()

  fun onPreviousPressed()

  fun onNextPressed()

  fun onStopPressed()

  fun onMutePressed()

  fun onShufflePressed()

  fun onRepeatPressed()

  fun onVolumeChange(volume: Int)

  fun onPositionChange(position: Int)

  fun onScrobbleToggle()

  fun onLfmLoveToggle()
}
