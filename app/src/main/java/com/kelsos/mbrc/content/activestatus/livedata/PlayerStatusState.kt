package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel

interface PlayerStatusState : State<PlayerStatusModel>

class PlayerStatusStateImpl : PlayerStatusState,
  BaseState<PlayerStatusModel>() {
  init {
    set(PlayerStatusModel())
  }
}