package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("code", "message", "description")
open class BaseResponse {

  @JsonProperty("code")
  var code: Int = 0

  @JsonProperty("message")
  var message: String = ""

  @JsonProperty("description")
  var description: String = ""

}
