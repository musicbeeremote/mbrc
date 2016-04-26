package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("enabled")
class ChangeStateRequest {

    @JsonProperty("enabled")
    private var enabled: Boolean? = null

    /**

     * @return
     * * The enabled
     */
    @JsonProperty("enabled")
    fun isEnabled(): Boolean? {
        return enabled
    }

    /**

     * @param enabled
     * * The enabled
     */
    @JsonProperty("enabled")
    fun setEnabled(enabled: Boolean?): ChangeStateRequest {
        this.enabled = enabled
        return this
    }

}
