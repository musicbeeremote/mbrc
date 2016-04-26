package com.kelsos.mbrc.dto.track

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("position", "duration")
class Position : BaseResponse() {

    /**
     * @return The position
     */
    /**
     * @param position The position
     */
    @JsonProperty("position") var position: Int = 0
    /**
     * @return The duration
     */
    /**
     * @param duration The duration
     */
    @JsonProperty("duration") var duration: Int = 0

}
