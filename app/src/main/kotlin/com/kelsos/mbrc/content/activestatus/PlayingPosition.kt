package com.kelsos.mbrc.content.activestatus

data class PlayingPosition(val current: Duration = 0, val total: Duration = 0) {
  fun totalMinutes(): String {
    return total.toMinutes()
  }

  fun currentMinutes(): String {
    return current.toMinutes()
  }
}

typealias Duration = Long

fun Duration.toMinutes(): String {
  val inSeconds = this / 1000
  val minutes = inSeconds / 60
  val seconds = inSeconds % 60
  return "%02d:%02d".format(minutes, seconds)
}
