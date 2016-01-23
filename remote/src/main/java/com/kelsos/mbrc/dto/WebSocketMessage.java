package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "message"
})
public class WebSocketMessage implements IMessage {

  @JsonProperty("message")
  private String message;

  /**
   * No args constructor for use in serialization.
   * Creates a new {@link WebSocketMessage}
   */
  public WebSocketMessage() {

  }

  /**
   * @param message
   */
  public WebSocketMessage(String message) {
    this.message = message;
  }

  /**
   * @return The message
   */
  @Override @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  /**
   * @param message The message
   */
  @Override @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(message).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof WebSocketMessage)) {
      return false;
    }
    WebSocketMessage rhs = ((WebSocketMessage) other);
    return new EqualsBuilder().append(message, rhs.message).isEquals();
  }

}
