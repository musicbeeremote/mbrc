package com.kelsos.mbrc.features.library.tracks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayingTrack(
  var artist: String = "",
  var title: String = "",
  var album: String = "",
  var year: String = "",
  var path: String = "",
  var coverUrl: String = "",
  var duration: Long = 0
) : Parcelable {
  fun artistInfo(): String {
    val artistBasic = "$artist - $album"
    return if (year.isEmpty()) {
      artistBasic
    } else {
      "$artistBasic ($year)"
    }
  }
}
