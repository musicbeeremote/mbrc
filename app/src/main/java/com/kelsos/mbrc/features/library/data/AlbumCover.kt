package com.kelsos.mbrc.features.library.data

import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okio.ByteString.Companion.encodeUtf8

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

fun AlbumCover.key(): String = "${artist}_$album".encodeUtf8().sha1().hex()
