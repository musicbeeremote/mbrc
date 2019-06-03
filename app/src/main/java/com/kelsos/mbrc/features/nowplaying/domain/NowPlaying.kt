package com.kelsos.mbrc.features.nowplaying.domain

data class NowPlaying(
  val title: String,
  val artist: String,
  val path: String,
  val position: Int,
  val id: Long
)