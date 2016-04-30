package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("message")
class WebSocketMessage : IMessage {

    /**
     * @return The message
     */
    /**
     * @param message The message
     */
    @JsonProperty("message")
    override var message: String = String.empty

    /**
     * No args constructor for use in serialization.
     * Creates a new [WebSocketMessage]
     */
    constructor() {

    }

    /**
     * @param message
     */
    constructor(message: String) {
        this.message = message
    }

}
