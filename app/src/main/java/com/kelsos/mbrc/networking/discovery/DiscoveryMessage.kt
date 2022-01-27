package com.kelsos.mbrc.networking.discovery

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoveryMessage(
  @Json(name = "name")
  var name: String = "",
  @Json(name = "address")
  var address: String = "",
  @Json(name = "port")
  var port: Int = 0,
  @Json(name = "context")
  var context: String = "",
)
