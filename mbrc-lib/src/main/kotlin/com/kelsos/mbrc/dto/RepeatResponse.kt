package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.Repeat.Mode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value", "code")
class RepeatResponse {

    @JsonProperty("value") var value: String? = null
    @JsonProperty("code") var code: Int = 0
}
