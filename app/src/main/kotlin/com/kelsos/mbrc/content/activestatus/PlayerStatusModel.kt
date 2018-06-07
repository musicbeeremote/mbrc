package com.kelsos.mbrc.content.activestatus

import androidx.annotation.IntRange
import com.kelsos.mbrc.events.ShuffleMode

data class PlayerStatusModel(
  @IntRange(from = 0, to = 100)
  val volume: Int = 0,
  val mute: Boolean = false,
  @ShuffleMode.Shuffle
  val shuffle: String = ShuffleMode.OFF,
  val scrobbling: Boolean = false,
  @Repeat.Mode
  val repeat: String = Repeat.NONE,
  @PlayerState.State
  var playState: String = PlayerState.UNDEFINED
)