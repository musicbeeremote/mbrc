package com.kelsos.mbrc.events

sealed class ShuffleMode(val mode: String) {
  object Off : ShuffleMode(OFF)
  object AutoDJ : ShuffleMode(AUTO_DJ)
  object Shuffle : ShuffleMode(SHUFFLE)

  companion object {
    const val OFF = "off"
    const val AUTO_DJ = "autodj"
    const val SHUFFLE = "shuffle"

    fun fromString(string: String): ShuffleMode = when (string) {
      AUTO_DJ -> AutoDJ
      SHUFFLE -> Shuffle
      else -> Off
    }
  }
}
