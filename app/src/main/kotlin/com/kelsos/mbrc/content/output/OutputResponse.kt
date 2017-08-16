package com.kelsos.mbrc.content.output

import com.fasterxml.jackson.annotation.JsonProperty

data class OutputResponse(

    @field:JsonProperty("devices")
    val devices: List<String> = emptyList(),

    @field:JsonProperty("active")
    val active: String = ""
)
