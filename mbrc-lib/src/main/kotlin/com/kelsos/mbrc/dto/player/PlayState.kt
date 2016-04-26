package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.empty


@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("value")
class PlayState : BaseResponse() {

    /**
     * @return The value
     */
    /**
     * @param value The value
     */
    @JsonProperty("value")
    @PlayerState.State
    var value: String = String.empty


}
