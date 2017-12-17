package com.kelsos.mbrc.content.library.covers

import androidx.room.ColumnInfo
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.utilities.RemoteUtils

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AlbumCover(
  @ColumnInfo
  @JsonProperty("artist")
  val artist: String,
  @ColumnInfo
  @JsonProperty("album")
  val album: String,
  @ColumnInfo
  @JsonProperty("hash")
  val hash: String?
)

fun AlbumCover.key(): String = RemoteUtils.sha1("${artist}_$album")
