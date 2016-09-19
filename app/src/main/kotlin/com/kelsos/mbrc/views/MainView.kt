package com.kelsos.mbrc.views

import android.graphics.Bitmap

import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.enums.LfmStatus
import com.kelsos.mbrc.events.ui.ShuffleChange

interface MainView : BaseView {

  fun updateCover(cover: Bitmap?)

  fun updateShuffleState(@ShuffleChange.ShuffleState shuffleState: String)

  fun updateRepeat(@Repeat.Mode mode: String)

  fun updateVolume(volume: Int, mute: Boolean)

  fun updatePlayState(@PlayerState.State state: String)

  fun updateTrackInfo(info: TrackInfo)

  fun updateConnection(status: Int)

  fun updateScrobbleStatus(active: Boolean)

  fun updateLfmStatus(status: LfmStatus)
}
