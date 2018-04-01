package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import javax.inject.Inject

interface PlayerStatusLiveDataProvider : LiveDataProvider<PlayerStatusModel>

class PlayerStatusLiveDataProviderImpl
@Inject
constructor() : PlayerStatusLiveDataProvider,
  BaseLiveDataProvider<PlayerStatusModel>() {
  init {
    update(PlayerStatusModel())
  }
}
