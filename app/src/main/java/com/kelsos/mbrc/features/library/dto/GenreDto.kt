package com.kelsos.mbrc.features.library.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenreDto(
  @Json(name = "genre")
  var genre: String = ""
)