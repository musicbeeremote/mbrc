package com.kelsos.mbrc.core.networking.protocol.payloads

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NowPlayingTrackRemoveResponse(
  @Json(name = "index")
  val index: Int,
  @Json(name = "success")
  val success: Boolean
)
