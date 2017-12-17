package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.interfaces.data.Data

interface Genre : Data {
  val genre: String
  val id: Long
}
