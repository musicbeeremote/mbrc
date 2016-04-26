package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

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
    override var message: String? = null

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

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(message).toHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is WebSocketMessage) {
            return false
        }
        return EqualsBuilder().append(message, other.message).isEquals
    }

}
