package com.kelsos.mbrc.cache

import com.kelsos.mbrc.annotations.Mute
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Shuffle

interface PlayerStateCache {
    @Shuffle.State var shuffle: String
    var volume: Int
    @PlayerState.State var playState: String
    @Mute.State var muteState: Int
    @Repeat.Mode var repeat: String
}
