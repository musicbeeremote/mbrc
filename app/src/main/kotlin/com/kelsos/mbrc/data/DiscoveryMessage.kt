package com.kelsos.mbrc.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscoveryMessage(@JsonProperty("name")
                            val name: String? = null,
                            @JsonProperty("address")
                            val address: String? = null,
                            @JsonProperty("port")
                            val port: Int = 0,
                            @JsonProperty("context")
                            val context: String? = "discovery")
