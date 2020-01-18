package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.features.library.PlayingTrack
import kotlinx.coroutines.runBlocking

interface PlayingTrackState : State<PlayingTrack>

class PlayingTrackStateImpl(
  private val playingTrackCache: PlayingTrackCache,
  appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseState<PlayingTrack>(),
  PlayingTrackState {
  init {
    set(PlayingTrack())

    runBlocking(appCoroutineDispatchers.disk) {
      with(playingTrackCache) {
        try {
          val coverUrl = restoreCover()
          val trackInfo = restoreInfo()

          if (trackInfo != null && coverUrl != null) {
            set(trackInfo.copy(coverUrl = coverUrl))
          }
        } catch (ex: Exception) {
        }
      }
    }
  }
}