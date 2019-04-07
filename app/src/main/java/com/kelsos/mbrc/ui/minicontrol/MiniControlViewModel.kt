package com.kelsos.mbrc.ui.minicontrol

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious

class MiniControlViewModel(
  val playingTrack: PlayingTrackState,
  val playerStatus: PlayerStatusState,
  val trackPosition: TrackPositionState,
  private val userActionUseCase: UserActionUseCase
) : ViewModel() {

  fun next() {
    userActionUseCase.perform(UserAction.create(PlayerNext))
  }

  fun previous() {
    userActionUseCase.perform(UserAction.create(PlayerPrevious))
  }

  fun playPause() {
    userActionUseCase.perform(UserAction.create(PlayerPlayPause))
  }
}