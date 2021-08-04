package com.kelsos.mbrc.common.state.models

data class PlayingPosition(val current: Duration = 0, val total: Duration = 0) {
  val totalMinutes get() = total.toMinutes()
  val currentMinutes get() = current.toMinutes()

  fun progress(): String {
    return "$currentMinutes / $totalMinutes"
  }
}

typealias Duration = Long

fun Duration.toMinutes(): String {
  val inSeconds = this / 1000
  val minutes = inSeconds / 60
  val seconds = inSeconds % 60
  return "%02d:%02d".format(minutes, seconds)
}
