package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.launch

interface PlayingTrackState : State<PlayingTrack>

class PlayingTrackStateImpl(
  private val playingTrackCache: PlayingTrackCache,
  appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseState<PlayingTrack>(), PlayingTrackState {
  init {
    set(PlayingTrack())

    scope.launch(appCoroutineDispatchers.disk) {
      with(playingTrackCache) {
        try {
          val coverUrl = restoreCover()
          val trackInfo = restoreInfo()

          set(trackInfo.copy(coverUrl = coverUrl))
        } catch (ex: Exception) {
        }
      }
    }
  }
}
