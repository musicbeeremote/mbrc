package com.kelsos.mbrc.core.networking.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QueuePayload(
  @Json(name = "queue")
  val type: String,
  @Json(name = "data")
  val data: List<String>,
  @Json(name = "play")
  val play: String? = null
)

@JsonClass(generateAdapter = true)
data class QueueResponse(@Json(name = "code") val code: Int)
