package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.interfaces.data.Data

interface Playlist : Data {
  val name: String
  val url: String
  val id: Long
}