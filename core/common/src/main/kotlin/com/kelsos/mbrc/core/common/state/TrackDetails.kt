package com.kelsos.mbrc.core.common.state

/**
 * Extended track details beyond the basic [TrackInfo].
 * Contains metadata and file properties from the currently playing track.
 */
data class TrackDetails(
  // Tag metadata
  val albumArtist: String = "",
  val genre: String = "",
  val trackNo: String = "",
  val trackCount: String = "",
  val discNo: String = "",
  val discCount: String = "",
  val grouping: String = "",
  val publisher: String = "",
  val ratingAlbum: String = "",
  val composer: String = "",
  val comment: String = "",
  val encoder: String = "",

  // File properties
  val kind: String = "",
  val format: String = "",
  val size: String = "",
  val channels: String = "",
  val sampleRate: String = "",
  val bitrate: String = "",
  val dateModified: String = "",
  val dateAdded: String = "",
  val lastPlayed: String = "",
  val playCount: String = "",
  val skipCount: String = "",
  val duration: String = ""
) {
  companion object {
    val EMPTY = TrackDetails()
  }

  /**
   * Returns true if this contains any meaningful data.
   */
  fun hasData(): Boolean = this != EMPTY

  /**
   * Formats the track/disc number as "track/total" or just "track" if no total.
   */
  fun formatTrackNumber(): String = when {
    trackNo.isBlank() -> ""
    trackCount.isBlank() -> trackNo
    else -> "$trackNo/$trackCount"
  }

  /**
   * Formats the disc number as "disc/total" or just "disc" if no total.
   */
  fun formatDiscNumber(): String = when {
    discNo.isBlank() -> ""
    discCount.isBlank() -> discNo
    else -> "$discNo/$discCount"
  }
}
