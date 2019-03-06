package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.runBlocking

interface PlayingTrackLiveDataProvider : LiveDataProvider<PlayingTrack>

class PlayingTrackLiveDataProviderImpl(
  private val playingTrackCache: PlayingTrackCache,
  appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseLiveDataProvider<PlayingTrack>(),
  PlayingTrackLiveDataProvider {
  init {
    update(PlayingTrack())

    runBlocking(appCoroutineDispatchers.disk) {
      with(playingTrackCache) {
        try {
          val coverUrl = restoreCover()
          val trackInfo = restoreInfo()

          if (trackInfo != null && coverUrl != null) {
            update(trackInfo.copy(coverUrl = coverUrl))
          }
        } catch (ex: Exception) {
        }
      }
    }
  }
}