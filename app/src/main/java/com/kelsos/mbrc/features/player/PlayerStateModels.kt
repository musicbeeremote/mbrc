package com.kelsos.mbrc.features.player

import com.kelsos.mbrc.common.state.PlayerState
import com.kelsos.mbrc.common.state.Repeat
import com.kelsos.mbrc.common.state.ShuffleMode

data class VolumeState(val volume: Int = 0, val mute: Boolean = false)

data class PlaybackState(
  val playerState: PlayerState = PlayerState.Undefined,
  val shuffle: ShuffleMode = ShuffleMode.Off,
  val repeat: Repeat = Repeat.None
)
