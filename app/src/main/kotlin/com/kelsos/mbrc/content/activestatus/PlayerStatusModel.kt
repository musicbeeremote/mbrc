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
  var state: String = PlayerState.UNDEFINED
) {
  fun isShuffleAutoDj(): Boolean = shuffle == ShuffleMode.AUTODJ

  fun isShuffleOff(): Boolean = shuffle == ShuffleMode.OFF

  fun isRepeatOff(): Boolean = repeat == Repeat.NONE

  fun isRepeatOne(): Boolean = repeat == Repeat.ONE

  fun isPlaying(): Boolean = state == PlayerState.PLAYING
}
