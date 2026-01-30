package com.kelsos.mbrc.core.common.state

import androidx.annotation.IntRange
import androidx.compose.runtime.Stable

@Stable
data class PlayerStatusModel(
  @get:IntRange(from = 0, to = 100)
  val volume: Int = 0,
  val mute: Boolean = false,
  val shuffle: ShuffleMode = ShuffleMode.Off,
  val scrobbling: Boolean = false,
  val repeat: Repeat = Repeat.None,
  val state: PlayerState = PlayerState.Undefined
)
