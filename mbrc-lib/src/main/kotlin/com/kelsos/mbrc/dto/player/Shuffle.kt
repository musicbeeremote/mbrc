package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("state")
class Shuffle : BaseResponse() {

    /**

     * @return
     * *     The state
     */
    /**

     * @param state
     * *     The state
     */
    @JsonProperty("state")
    @com.kelsos.mbrc.annotations.Shuffle.State
    var state: String? = null

}
