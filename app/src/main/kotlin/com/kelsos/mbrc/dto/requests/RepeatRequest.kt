package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.Repeat
import com.kelsos.mbrc.annotations.Repeat.Mode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("mode")
class RepeatRequest {

    @Mode
    @JsonProperty("mode")
    var mode: String = Repeat.NONE

}
