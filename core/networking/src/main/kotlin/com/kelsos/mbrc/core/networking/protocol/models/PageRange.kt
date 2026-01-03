package com.kelsos.mbrc.core.networking.protocol.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PageRange {
  @Json(name = "offset")
  var offset: Int = 0

  @Json(name = "limit")
  var limit: Int = 0
}
