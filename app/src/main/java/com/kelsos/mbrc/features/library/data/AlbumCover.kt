package com.kelsos.mbrc.features.library.data

import androidx.room.ColumnInfo
import com.kelsos.mbrc.common.utilities.RemoteUtils.sha1
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

fun AlbumCover.key(): String = sha1("${artist}_$album")
