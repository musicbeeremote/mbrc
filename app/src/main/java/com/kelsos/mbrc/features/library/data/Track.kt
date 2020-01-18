package com.kelsos.mbrc.features.library.data

import com.kelsos.mbrc.common.utilities.RemoteUtils.sha1

data class Track(
  var artist: String,
  var title: String,
  var src: String,
  var trackno: Int,
  var disc: Int,
  var albumArtist: String,
  var album: String,
  var genre: String,
  var year: String,
  var id: Long
)

fun Track.key(): String = sha1("${albumArtist}_$album")
