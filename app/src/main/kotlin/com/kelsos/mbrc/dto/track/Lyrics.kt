package com.kelsos.mbrc.dto.track

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("lyrics")
class Lyrics : BaseResponse() {
    @JsonProperty("lyrics") var lyrics: String = String.empty
}
