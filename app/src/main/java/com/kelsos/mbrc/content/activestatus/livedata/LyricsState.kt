package com.kelsos.mbrc.content.activestatus.livedata

interface LyricsState : State<List<String>>

class LyricsStateImpl : LyricsState, BaseState<List<String>>() {
  init {
    set(emptyList())
  }
}
