package com.kelsos.mbrc.common.state.domain

sealed class PlayerState(val state: String) {
  object Playing : PlayerState(PLAYING)
  object Paused : PlayerState(PAUSED)
  object Stopped : PlayerState(STOPPED)
  object Undefined : PlayerState(UNDEFINED)

  companion object {
    const val PLAYING = "playing"
    const val PAUSED = "paused"
    const val STOPPED = "stopped"
    const val UNDEFINED = "undefined"

    fun fromString(state: String): PlayerState = when {
      PLAYING.equals(state, ignoreCase = true) -> Playing
      PAUSED.equals(state, ignoreCase = true) -> Paused
      STOPPED.equals(state, ignoreCase = true) -> Stopped
      else -> Undefined
    }
  }
}
