package com.kelsos.mbrc.dto.track

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("rating")
class Rating : BaseResponse() {

    @JsonProperty("rating")
    var rating: Double = 0.toDouble()

}
