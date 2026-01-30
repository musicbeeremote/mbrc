package com.kelsos.mbrc.core.common.state

import androidx.compose.runtime.Stable

/**
 * Interface representing track information.
 * Platform-specific implementations (like Parcelable) should implement this.
 */
@Stable
interface TrackInfo {
  val artist: String
  val title: String
  val album: String
  val year: String
  val path: String
  val coverUrl: String
  val duration: Long
}

/**
 * Simple immutable implementation of TrackInfo for use in core modules.
 */
@Stable
data class BasicTrackInfo(
  override val artist: String = "",
  override val title: String = "",
  override val album: String = "",
  override val year: String = "",
  override val path: String = "",
  override val coverUrl: String = "",
  override val duration: Long = 0
) : TrackInfo

fun TrackInfo?.orEmpty(): TrackInfo = this ?: BasicTrackInfo()

/**
 * Converts any TrackInfo implementation to a BasicTrackInfo.
 * Useful for when you need to use .copy() which requires a data class.
 */
fun TrackInfo.toBasicTrackInfo(): BasicTrackInfo = when (this) {
  is BasicTrackInfo -> this

  else -> BasicTrackInfo(
    artist = artist,
    title = title,
    album = album,
    year = year,
    path = path,
    coverUrl = coverUrl,
    duration = duration
  )
}
