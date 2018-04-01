package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.TrackPositionData
import javax.inject.Inject

interface TrackPositionLiveDataProvider : LiveDataProvider<TrackPositionData>

class TrackPositionLiveDataProviderImpl
@Inject constructor() : TrackPositionLiveDataProvider, BaseLiveDataProvider<TrackPositionData>() {
  init {
    update(TrackPositionData())
  }

}