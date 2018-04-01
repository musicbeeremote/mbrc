package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface PlayingTrackLiveDataProvider : LiveDataProvider<PlayingTrackModel>

class PlayingTrackLiveDataProviderImpl
@Inject
constructor(
  private val playingTrackCache: PlayingTrackCache
) : BaseLiveDataProvider<PlayingTrackModel>(),
  PlayingTrackLiveDataProvider {
  init {
    update(PlayingTrackModel())
    runBlocking {
      with(playingTrackCache) {
        val coverUrl = restoreCover()
        val trackInfo = restoreInfo()
        update(trackInfo.copy(coverUrl = coverUrl))
      }
    }
  }
}
