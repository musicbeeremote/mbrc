package com.kelsos.mbrc.ui.navigation.main

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.mvp.BaseView

interface MainView : BaseView {
  fun updateCover(bitmap: Bitmap?)

  fun updateShuffle(@Shuffle.State state: String)

  fun updateRepeat(@Repeat.Mode mode: String)

  fun updateScrobbling(enabled: Boolean)

  fun updateLoved(status: LfmStatus)

  fun updateVolume(volume: Int)

  fun updatePlayState(@PlayerState.State playstate: String)

  fun updateMute(enabled: Boolean)

  fun updatePosition(position: TrackPosition)

  val currentProgress: Int

  fun setStoppedState()

  fun updateTrackInfo(trackInfo: TrackInfo)

}

interface MainViewPresenter : Presenter<MainView> {

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

interface MainViewModel {
  var shuffle: String

  var playState: String

  var repeat: String

  var isMuted: Boolean

  var trackInfo: TrackInfo

  var trackCover: Bitmap?

  var position: TrackPosition

  var rating: Float

  var volume: Int

  val isLoaded: Boolean

  fun loadComplete()
}
