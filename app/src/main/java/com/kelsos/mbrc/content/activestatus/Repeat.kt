package com.kelsos.mbrc.content.activestatus

sealed class Repeat(val mode: String) {
  object All : Repeat(ALL)
  object None : Repeat(NONE)
  object One : Repeat(ONE)

  companion object {
    const val ALL = "all"
    const val NONE = "none"
    const val ONE = "one"

    fun fromString(mode: String): Repeat = when (mode) {
      ALL -> All
      ONE -> One
      else -> None
    }
  }
}
