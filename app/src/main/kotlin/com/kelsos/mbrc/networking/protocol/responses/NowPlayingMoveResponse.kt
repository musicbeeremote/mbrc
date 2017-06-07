package com.kelsos.mbrc.networking.protocol.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class NowPlayingMoveResponse(
    @JsonProperty("from") val from: Int,
    @JsonProperty("to") val to: Int,
    @JsonProperty("success") val success: Boolean = false
)
