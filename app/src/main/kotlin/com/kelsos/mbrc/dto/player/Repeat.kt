package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.Repeat.Mode
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("value")
class Repeat : BaseResponse() {
  @JsonProperty("value")
  @Mode
  var value: String = com.kelsos.mbrc.annotations.Repeat.UNDEFINED
}
