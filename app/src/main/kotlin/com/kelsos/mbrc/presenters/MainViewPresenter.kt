package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.mvp.IPresenter
import com.kelsos.mbrc.ui.views.MainView

interface MainViewPresenter : IPresenter<MainView> {

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
