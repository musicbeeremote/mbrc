package com.kelsos.mbrc.content.nowplaying

data class NowPlaying(
  val title: String,
  val artist: String,
  val path: String,
  val position: Int,
  val id: Long
)