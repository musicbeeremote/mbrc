package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.Repeat

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("mode")
class RepeatRequest {

    @Repeat.Mode
    @JsonProperty("mode")
    var mode: String = Repeat.UNDEFINED

}
