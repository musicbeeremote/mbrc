package com.kelsos.mbrc.rest.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "position"
})
public class PositionRequest {

  @JsonProperty("position") private int position;

  /**
   * @return The position
   */
  @JsonProperty("position") public int getPosition() {
    return position;
  }

  /**
   * @param position The position
   */
  @JsonProperty("position") public PositionRequest setPosition(int position) {
    this.position = position;
    return this;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(position).toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PositionRequest)) {
      return false;
    }
    PositionRequest rhs = ((PositionRequest) other);
    return new EqualsBuilder().append(position, rhs.position).isEquals();
  }
}
