package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Data

interface Album : Data {
  val artist: String
  val album: String
  val id: Long
}
