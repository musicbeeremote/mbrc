package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.Shuffle
import com.kelsos.mbrc.annotations.Shuffle.State
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("state")
class Shuffle : BaseResponse() {

  @JsonProperty("state")
  @State
  var state: String = Shuffle.OFF

}
