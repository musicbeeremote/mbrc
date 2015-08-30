package com.kelsos.mbrc.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.annotations.ShuffleState;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "state",
    "success"
})

public class SuccessStateResponse {

  @ShuffleState
  @JsonProperty("state") private String state;
  @JsonProperty("success") private boolean success;

  /**
   * @return The state
   */
  @ShuffleState
  @JsonProperty("state") public String getState() {
    return state;
  }

  /**
   * @param state The state
   */
  @ShuffleState
  @JsonProperty("state") public void setState(String state) {
    this.state = state;
  }

  /**
   * @return The success
   */
  @JsonProperty("success") public boolean isSuccess() {
    return success;
  }

  /**
   * @param success The success
   */
  @JsonProperty("success") public SuccessStateResponse setSuccess(boolean success) {
    this.success = success;
    return this;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(state).append(success).toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof SuccessStateResponse)) {
      return false;
    }
    SuccessStateResponse rhs = ((SuccessStateResponse) other);
    return new EqualsBuilder().append(state, rhs.state).append(success, rhs.success).isEquals();
  }
}


