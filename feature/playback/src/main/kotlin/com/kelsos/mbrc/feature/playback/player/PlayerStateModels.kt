package com.kelsos.mbrc.feature.playback.player

import androidx.compose.runtime.Stable
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.Repeat
import com.kelsos.mbrc.core.common.state.ShuffleMode

@Stable
data class VolumeState(val volume: Int = 0, val mute: Boolean = false)

@Stable
data class PlaybackState(
  val playerState: PlayerState = PlayerState.Undefined,
  val shuffle: ShuffleMode = ShuffleMode.Off,
  val repeat: Repeat = Repeat.None
)
