package com.kelsos.mbrc.features.library

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlayingTrack(
  var artist: String = "",
  var title: String = "",
  var album: String = "",
  var year: String = "",
  var path: String = "",
  var coverUrl: String = ""
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