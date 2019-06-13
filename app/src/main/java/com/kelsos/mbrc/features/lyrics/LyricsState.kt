package com.kelsos.mbrc.features.lyrics

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State

interface LyricsState : State<List<String>>

class LyricsStateImpl : LyricsState, BaseState<List<String>>() {
  init {
    set(emptyList())
  }
}