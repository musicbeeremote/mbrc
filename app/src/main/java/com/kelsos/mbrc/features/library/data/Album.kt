package com.kelsos.mbrc.features.library.data

import okio.ByteString.Companion.encodeUtf8

data class Album(
  var id: Long,
  var artist: String,
  var album: String,
  val cover: String?
)

fun Album.key(): String = "${artist}_$album".encodeUtf8().sha1().hex()
