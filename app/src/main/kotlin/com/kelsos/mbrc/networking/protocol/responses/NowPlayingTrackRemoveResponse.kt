package com.kelsos.mbrc.networking.protocol.responses

import com.fasterxml.jackson.annotation.JsonProperty

data class NowPlayingTrackRemoveResponse(
    @JsonProperty("index") val index: Int,
    @JsonProperty("success") val success: Boolean
)
