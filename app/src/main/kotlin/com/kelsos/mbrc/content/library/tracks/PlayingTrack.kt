package com.kelsos.mbrc.content.library.tracks

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
  fun albumInfo():String {
    return if (year.isEmpty()) {
      album
    } else {
      "$album ($year)"
    }
  }
}