package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("path")
class PlayPathRequest {

    @JsonProperty("path")
    var path: String = String.empty

}
