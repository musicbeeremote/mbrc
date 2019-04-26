package com.kelsos.mbrc.features.playlists

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistDto(
  @Json(name = "name")
  var name: String = "",
  @Json(name = "url")
  var url: String = ""
)
