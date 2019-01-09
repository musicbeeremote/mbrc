package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.Data

interface NowPlaying : Data {
  val title: String
  val artist: String
  val path: String
  val position: Int
  val id: Long
}