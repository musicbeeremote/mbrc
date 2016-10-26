package com.kelsos.mbrc.cache

import android.support.annotation.IntRange
import com.kelsos.mbrc.annotations.Mute
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle

import com.kelsos.mbrc.annotations.Shuffle.State
import javax.inject.Inject

class PlayerStateCacheImpl
@Inject
constructor() : PlayerStateCache {

  @State override var shuffle: String = Shuffle.UNDEF
  @IntRange(from = -1, to = 100) override var volume: Int = -1
  @PlayerState.State override var playState: String = PlayerState.UNDEFINED
  @Mute.State override var muteState: Int = Mute.UNDEF
  @Repeat.Mode override var repeat: String = Repeat.UNDEFINED

}
