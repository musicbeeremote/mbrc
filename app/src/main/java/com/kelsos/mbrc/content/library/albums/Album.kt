package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Data
import com.kelsos.mbrc.utilities.RemoteUtils.sha1

interface Album : Data {
  val artist: String
  val album: String
  val cover: String?
  val id: Long
}

fun Album.key(): String = sha1("${artist}_$album")
