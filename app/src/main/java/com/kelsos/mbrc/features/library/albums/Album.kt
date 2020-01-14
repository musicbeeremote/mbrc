package com.kelsos.mbrc.features.library.albums

import com.kelsos.mbrc.utilities.RemoteUtils

data class Album(
  var id: Long,
  var artist: String,
  var album: String,
  val cover: String?
)

fun Album.key(): String = RemoteUtils.sha1("${artist}_$album")
