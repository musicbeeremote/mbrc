package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

interface TrackPositionState : State<PlayingPosition> {
  fun setPlaying(playing: Boolean)
}

class TrackPositionStateImpl() : TrackPositionState, BaseState<PlayingPosition>() {

  init {
    set(PlayingPosition())
  }

  private var timer: Timer? = null

  private fun running(): Boolean {
    return this.timer != null
  }

  override fun setPlaying(playing: Boolean) {
    if (playing) {
      startUpdatingProgress()
    } else {
      timer?.cancel()
      timer = null
    }
  }

  private fun startUpdatingProgress() {
    if (running()) {
      return
    }

    timer = fixedRateTimer(
      period = 1000,
      initialDelay = 1000
    ) {
      set {
        copy(current = current + 1000)
      }
    }
  }
}
