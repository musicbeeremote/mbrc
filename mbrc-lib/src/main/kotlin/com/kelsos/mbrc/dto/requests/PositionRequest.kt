package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("position")
class PositionRequest {

    @JsonProperty("position") private var position: Int = 0

    /**
     * @return The position
     */
    @JsonProperty("position") fun getPosition(): Int {
        return position
    }

    /**
     * @param position The position
     */
    @JsonProperty("position") fun setPosition(position: Int): PositionRequest {
        this.position = position
        return this
    }

}
