package com.kelsos.mbrc.common.state.models

import androidx.annotation.IntRange
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.domain.Repeat
import com.kelsos.mbrc.events.ShuffleMode

data class PlayerStatusModel(
  @get:IntRange(from = 0, to = 100)
  val volume: Int = 0,
  val mute: Boolean = false,
  val shuffle: ShuffleMode = ShuffleMode.Off,
  val scrobbling: Boolean = false,
  val repeat: Repeat = Repeat.None,
  var state: PlayerState = PlayerState.Undefined,
)
