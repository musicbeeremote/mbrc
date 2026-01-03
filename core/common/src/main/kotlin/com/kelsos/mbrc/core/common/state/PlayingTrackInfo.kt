package com.kelsos.mbrc.core.common.state

/**
 * Core playing track information without platform dependencies.
 * Use this in modules that don't need Parcelable or MediaItem conversion.
 */
data class PlayingTrackInfo(
  val artist: String = "",
  val title: String = "",
  val album: String = "",
  val year: String = "",
  val path: String = "",
  val coverUrl: String = "",
  val duration: Long = 0
)

fun PlayingTrackInfo?.orEmpty() = this ?: PlayingTrackInfo()
