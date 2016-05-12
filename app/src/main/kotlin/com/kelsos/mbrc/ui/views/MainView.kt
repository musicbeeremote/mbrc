package com.kelsos.mbrc.ui.views

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle.State
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.mvp.IView

interface MainView : IView {
  fun updateCover(bitmap: Bitmap?)

  fun updateShuffle(@State state: String)

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
