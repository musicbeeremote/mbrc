package com.kelsos.mbrc.metrics

data class SyncedData(
  val genres: Long,
  val artists: Long,
  val albums: Long,
  val tracks: Long,
  val playlists: Long,
) {
  companion object
}

fun SyncedData.Companion.empty(): SyncedData = SyncedData(-1, -1, -1, -1, -1)

fun SyncedData.isEmpty(): Boolean =
  this.genres == -1L &&
    this.artists == -1L &&
    this.albums == -1L &&
    this.tracks == -1L &&
    this.playlists == -1L
