package com.kelsos.mbrc.common.state

import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.state.models.PlayingPosition
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.features.library.PlayingTrack
import kotlinx.coroutines.flow.MutableStateFlow

class AppState {
  val playerStatus = MutableStateFlow(PlayerStatusModel())
  val playingTrack = MutableStateFlow(PlayingTrack())
  val playingTrackRating = MutableStateFlow(TrackRating())
  val playingPosition = MutableStateFlow(PlayingPosition())
  val lyrics = MutableStateFlow(emptyList<String>())
}
