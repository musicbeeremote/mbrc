package com.kelsos.mbrc.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("no_broadcast", "protocol_version")
class ProtocolPayload {

  @JsonProperty("no_broadcast")
  var noBroadcast: Boolean? = null
  @JsonProperty("protocol_version")
  var protocolVersion: Float? = null
}
