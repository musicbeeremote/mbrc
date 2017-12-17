package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Data

interface Artist : Data {
  val artist: String
  val id: Long
}
