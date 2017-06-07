package com.kelsos.mbrc.networking.protocol.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class Position(
  @JsonProperty("current") val current: Long,
  @JsonProperty("total") val total: Long
)
