package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayingPosition
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

interface TrackPositionLiveDataProvider : LiveDataProvider<PlayingPosition> {
  fun setPlaying(playing: Boolean)
}

class TrackPositionLiveDataProviderImpl
@Inject
constructor() : TrackPositionLiveDataProvider, BaseLiveDataProvider<PlayingPosition>() {

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
      update {
        copy(current = current + 1000)
      }
    }
  }

  init {
    update(PlayingPosition())
  }
}
