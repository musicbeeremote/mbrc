package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("message")
class WebSocketMessage : IMessage {

  @JsonProperty("message")
  override var message: String = String.empty

  constructor() {

  }

  constructor(message: String) {
    this.message = message
  }

  override fun toString(): String {
    return "WebSocketMessage(message='$message')"
  }

}
