package com.kelsos.mbrc.covers

import androidx.room.ColumnInfo
import com.kelsos.mbrc.utilities.RemoteUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumCover(
  @ColumnInfo
  @Json(name = "artist")
  val artist: String,
  @ColumnInfo
  @Json(name = "album")
  val album: String,
  @ColumnInfo
  @Json(name = "hash")
  val hash: String?
)

fun AlbumCover.key(): String = RemoteUtils.sha1("${artist}_$album")
