package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("from", "to")
class MoveRequest {

    @JsonProperty("from") private var from: Int = 0
    @JsonProperty("to") private var to: Int = 0

    /**
     * @return The from
     */
    @JsonProperty("from") fun getFrom(): Int {
        return from
    }

    /**
     * @param from The from
     */
    @JsonProperty("from") fun setFrom(from: Int): MoveRequest {
        this.from = from
        return this
    }

    /**
     * @return The to
     */
    @JsonProperty("to") fun getTo(): Int {
        return to
    }

    /**
     * @param to The to
     */
    @JsonProperty("to") fun setTo(to: Int): MoveRequest {
        this.to = to
        return this
    }
}
