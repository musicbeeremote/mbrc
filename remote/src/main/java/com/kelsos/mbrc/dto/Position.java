package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "position",
    "duration"
}) public class Position {

  @JsonProperty("position") private long position;
  @JsonProperty("duration") private long duration;

  /**
   * @return The position
   */
  @JsonProperty("position") public long getPosition() {
    return position;
  }

  /**
   * @param position The position
   */
  @JsonProperty("position") public void setPosition(long position) {
    this.position = position;
  }

  /**
   * @return The duration
   */
  @JsonProperty("duration") public long getDuration() {
    return duration;
  }

  /**
   * @param duration The duration
   */
  @JsonProperty("duration") public void setDuration(int duration) {
    this.duration = duration;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(position)
        .append(duration)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Position)) {
      return false;
    }
    Position rhs = ((Position) other);
    return new EqualsBuilder().append(position, rhs.position)
        .append(duration, rhs.duration)
        .isEquals();
  }
}
