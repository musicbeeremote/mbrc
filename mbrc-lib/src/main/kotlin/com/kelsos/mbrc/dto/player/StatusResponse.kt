package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("enabled", "success")
class StatusResponse : BaseResponse() {

    /**
     * @return The enabled
     */
    /**
     * @param enabled The enabled
     */
    @JsonProperty("enabled")
    var enabled: Boolean? = null
}

