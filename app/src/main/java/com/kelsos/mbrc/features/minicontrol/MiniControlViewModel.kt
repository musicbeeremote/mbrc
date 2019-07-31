package com.kelsos.mbrc.features.minicontrol

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.next
import com.kelsos.mbrc.networking.client.playPause
import com.kelsos.mbrc.networking.client.previous

class MiniControlViewModel(
  val playingTrack: PlayingTrackState,
  val playerStatus: PlayerStatusState,
  val trackPosition: TrackPositionState,
  private val userActionUseCase: UserActionUseCase
) : ViewModel() {

  fun next() {
    userActionUseCase.next()
  }

  fun previous() {
    userActionUseCase.previous()
  }

  fun playPause() {
    userActionUseCase.playPause()
  }
}