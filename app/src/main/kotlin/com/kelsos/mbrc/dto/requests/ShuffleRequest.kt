package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.Shuffle

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("status")
class ShuffleRequest {

    @JsonProperty("status")
    @Shuffle.State
    var status: String = Shuffle.UNDEF
}
