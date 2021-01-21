package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.preferences.AppDataStore
import kotlinx.coroutines.runBlocking

interface PlayingTrackState : State<PlayingTrack>

class PlayingTrackStateImpl(
  private val appDataStore: AppDataStore,
  appDispatchers: AppDispatchers
) : BaseState<PlayingTrack>(),
  PlayingTrackState {
  init {
    set(PlayingTrack())

    runBlocking(appDispatchers.io) {
      val fromCache = appDataStore.restoreFromCache()
      set(fromCache)
    }
  }
}
