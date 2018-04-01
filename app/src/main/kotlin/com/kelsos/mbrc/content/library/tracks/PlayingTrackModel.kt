package com.kelsos.mbrc.content.library.tracks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayingTrackModel(
  var artist: String = "",
  var title: String = "",
  var album: String = "",
  var year: String = "",
  var path: String = "",
  var coverUrl: String = "",
  var duration: Long = 0
) : Parcelable
