package com.kelsos.mbrc.core.networking.discovery

import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscoveryMessage(
  @Json(name = "name")
  val name: String = "",
  @Json(name = "address")
  val address: String = "",
  @Json(name = "port")
  val port: Int = 0,
  @Json(name = "context")
  val context: String = ""
)

fun DiscoveryMessage.toConnection(): ConnectionSettings = ConnectionSettings(
  address = address,
  port = port,
  name = name,
  isDefault = false,
  id = 0
)
