package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface TrackPositionState : State<PlayingPosition> {
  fun setPlaying(playing: Boolean)
}

class TrackPositionStateImpl(
  dispatchers: AppCoroutineDispatchers
) : TrackPositionState, BaseState<PlayingPosition>() {
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.io)
  private var positionUpdaterJob: Job? = null

  init {
    set(PlayingPosition())
  }

  private fun running(): Boolean {
    return positionUpdaterJob?.isActive == true
  }

  override fun setPlaying(playing: Boolean) {
    if (playing) {
      startUpdatingProgress()
    } else {
      positionUpdaterJob?.cancel()
    }
  }

  private fun startUpdatingProgress() {
    if (running()) {
      return
    }

    positionUpdaterJob = flow<Int> {
      delay(1000)
    }.onEach {
      set {
        copy(current = current + 1000)
      }
    }.launchIn(scope)
  }

  init {
    set(PlayingPosition())
  }
}
