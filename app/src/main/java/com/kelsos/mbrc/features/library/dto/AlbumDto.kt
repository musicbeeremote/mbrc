package com.kelsos.mbrc.features.library.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumDto(
  @Json(name = "artist")
  var artist: String = "",
  @Json(name = "album")
  var album: String = "",
)
