package com.kelsos.mbrc.networking

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscoveryMessage(
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("address")
    var address: String = "",
    @JsonProperty("port")
    var port: Int = 0,
    @JsonProperty("context")
    var context: String = ""
)
