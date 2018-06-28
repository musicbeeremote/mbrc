package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.TrackRating

interface TrackRatingLiveDataProvider : LiveDataProvider<TrackRating>

class TrackRatingLiveDataProviderImpl : TrackRatingLiveDataProvider,
  BaseLiveDataProvider<TrackRating>() {
  init {
    update(TrackRating())
  }
}
