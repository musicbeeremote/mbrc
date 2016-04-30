package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("name", "list")
class PlaylistRequest {

    @JsonProperty("name")
    var name: String = String.empty

    @JsonProperty("list")
    var list: List<String> = ArrayList()
}
