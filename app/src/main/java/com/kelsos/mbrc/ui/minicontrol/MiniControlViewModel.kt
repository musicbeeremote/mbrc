package com.kelsos.mbrc.ui.minicontrol

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious

class MiniControlViewModel(
  val playingTrack: PlayingTrackLiveDataProvider,
  val playerStatus: PlayerStatusLiveDataProvider,
  val trackPosition: TrackPositionLiveDataProvider,
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