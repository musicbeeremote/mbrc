package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.TrackRatingModel
import javax.inject.Inject

interface TrackRatingLiveDataProvider : LiveDataProvider<TrackRatingModel>

class TrackRatingLiveDataProviderImpl
@Inject
constructor() : TrackRatingLiveDataProvider,
  BaseLiveDataProvider<TrackRatingModel>() {
  init {
    update(TrackRatingModel())
  }
}