package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

interface TrackPositionState : State<PlayingPosition> {
  fun setPlaying(playing: Boolean)
}

class TrackPositionStateImpl(
  private val appRxSchedulers: AppRxSchedulers
) : TrackPositionState, BaseState<PlayingPosition>() {

  init {
    set(PlayingPosition())
  }

  private var disposable: Disposable? = null

  private fun running(): Boolean {
    val disposable = this.disposable
    return if (disposable == null) false else !disposable.isDisposed
  }

  override fun setPlaying(playing: Boolean) {
    if (playing) {
      startUpdatingProgress()
    } else {
      disposable?.dispose()
    }
  }

  private fun startUpdatingProgress() {
    if (running()) {
      return
    }

    disposable = Observable.interval(1, TimeUnit.SECONDS, appRxSchedulers.network).subscribe {
      set {
        copy(current = current + 1000)
      }
    }
  }

  init {
    set(PlayingPosition())
  }
}