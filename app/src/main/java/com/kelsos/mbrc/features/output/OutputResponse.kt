package com.kelsos.mbrc.features.output

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OutputResponse(
  @field:Json(name = "devices")
  val devices: List<String> = emptyList(),
  @field:Json(name = "active")
  val active: String = "",
)
