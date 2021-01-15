package com.kelsos.mbrc.content.activestatus

import androidx.annotation.IntRange
import com.kelsos.mbrc.events.ShuffleMode

data class PlayerStatusModel(
  @get:IntRange(from = 0, to = 100)
  val volume: Int = 0,
  val mute: Boolean = false,
  val shuffle: ShuffleMode = ShuffleMode.Off,
  val scrobbling: Boolean = false,
  val repeat: Repeat = Repeat.None,
  var state: PlayerState = PlayerState.Undefined
) {
  fun isShuffleAutoDj(): Boolean = shuffle == ShuffleMode.AutoDJ

  fun isShuffleOff(): Boolean = shuffle == ShuffleMode.Off

  fun isRepeatOff(): Boolean = repeat == Repeat.None

  fun isRepeatOne(): Boolean = repeat == Repeat.One

  fun isPlaying(): Boolean = state == PlayerState.Playing
}
