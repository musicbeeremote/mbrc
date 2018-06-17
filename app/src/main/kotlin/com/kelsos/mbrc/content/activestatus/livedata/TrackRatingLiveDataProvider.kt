package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.TrackRating
import javax.inject.Inject

interface TrackRatingLiveDataProvider : LiveDataProvider<TrackRating>

class TrackRatingLiveDataProviderImpl
@Inject
constructor() : TrackRatingLiveDataProvider,
  BaseLiveDataProvider<TrackRating>() {
  init {
    update(TrackRating())
  }
}
