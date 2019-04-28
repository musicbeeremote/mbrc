package com.kelsos.mbrc.features.nowplaying

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NowPlayingDto(
  @Json(name = "title")
  var title: String = "",
  @Json(name = "artist")
  var artist: String = "",
  @Json(name = "path")
  var path: String = "",
  @Json(name = "position")
  var position: Int = 0
)