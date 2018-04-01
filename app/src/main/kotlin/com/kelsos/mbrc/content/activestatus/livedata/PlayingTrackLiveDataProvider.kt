package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.rx2.awaitFirstOrNull
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
    async {

      with(playingTrackCache) {
        val coverUrl = restoreCover().toObservable().awaitFirstOrNull()
        val trackInfo = restoreInfo().toObservable().awaitFirstOrNull()

        if (trackInfo != null && coverUrl != null) {
          update(trackInfo.copy(coverUrl = coverUrl))
        }
      }
    }
  }
}