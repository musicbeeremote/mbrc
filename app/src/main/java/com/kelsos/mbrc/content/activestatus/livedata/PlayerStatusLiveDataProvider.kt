package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel

interface PlayerStatusLiveDataProvider : LiveDataProvider<PlayerStatusModel>

class PlayerStatusLiveDataProviderImpl : PlayerStatusLiveDataProvider,
  BaseLiveDataProvider<PlayerStatusModel>() {
  init {
    update(PlayerStatusModel())
  }
}