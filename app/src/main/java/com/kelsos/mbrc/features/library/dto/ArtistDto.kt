package com.kelsos.mbrc.features.library.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDto(
  @Json(name = "artist")
  var artist: String = ""
)
