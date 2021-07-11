package com.kelsos.mbrc.common.state.domain

sealed class Repeat(val mode: String) {
  object All : Repeat(ALL)
  object None : Repeat(NONE)
  object One : Repeat(ONE)

  companion object {
    const val ALL = "all"
    const val NONE = "none"
    const val ONE = "one"

    fun fromString(mode: String): Repeat = when {
      ALL.equals(mode, ignoreCase = true) -> All
      ONE.equals(mode, ignoreCase = true) -> One
      else -> None
    }
  }
}
