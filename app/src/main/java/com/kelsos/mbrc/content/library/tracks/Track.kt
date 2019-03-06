package com.kelsos.mbrc.content.library.tracks

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