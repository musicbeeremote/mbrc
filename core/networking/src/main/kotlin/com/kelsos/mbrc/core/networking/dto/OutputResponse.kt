package com.kelsos.mbrc.core.networking.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OutputResponse(
  @Json(name = "devices")
  val devices: List<String> = emptyList(),
  @Json(name = "active")
  val active: String = ""
)
