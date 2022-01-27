package com.kelsos.mbrc.features.library.data

import okio.ByteString.Companion.encodeUtf8

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
  var id: Long,
)

fun Track.key(): String = "${albumArtist}_$album".encodeUtf8().sha1().hex()
