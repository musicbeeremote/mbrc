package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("value")
class Repeat : BaseResponse() {

    /**
     * @return The value
     */
    /**
     * @param value The value
     */
    @JsonProperty("value")
    @com.kelsos.mbrc.annotations.Repeat.Mode
    var value: String? = null

}
