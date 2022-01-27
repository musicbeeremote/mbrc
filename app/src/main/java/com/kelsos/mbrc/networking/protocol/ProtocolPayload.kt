package com.kelsos.mbrc.networking.protocol

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProtocolPayload(
  @Json(name = "client_id")
  var clientId: String,
  @Json(name = "no_broadcast")
  var noBroadcast: Boolean = false,
  @Json(name = "protocol_version")
  var protocolVersion: Int = 3,
)
