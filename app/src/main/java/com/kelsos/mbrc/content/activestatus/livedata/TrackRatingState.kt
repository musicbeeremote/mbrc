package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.content.activestatus.TrackRating

interface TrackRatingState : State<TrackRating>

class TrackRatingStateImpl : TrackRatingState,
  BaseState<TrackRating>() {

  init {
    set(TrackRating())
  }
}
