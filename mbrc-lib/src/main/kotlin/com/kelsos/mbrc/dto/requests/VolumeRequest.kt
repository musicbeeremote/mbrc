package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("value")
class VolumeRequest {

    @JsonProperty("value")
    private var value: Int = 0

    /**

     * @return
     * * The value
     */
    @JsonProperty("value")
    fun getValue(): Int {
        return value
    }

    /**

     * @param value
     * * The value
     */
    @JsonProperty("value")
    fun setValue(value: Int): VolumeRequest {
        this.value = value
        return this
    }
}
