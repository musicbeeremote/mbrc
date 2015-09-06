package com.kelsos.mbrc.rest.responses;

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
}) public class TrackPositionResponse {

  @JsonProperty("position") private int position;
  @JsonProperty("duration") private Integer duration;

  /**
   * @return The position
   */
  @JsonProperty("position") public int getPosition() {
    return position;
  }

  /**
   * @param position The position
   */
  @JsonProperty("position") public void setPosition(int position) {
    this.position = position;
  }

  /**
   * @return The duration
   */
  @JsonProperty("duration") public int getDuration() {
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
    if (!(other instanceof TrackPositionResponse)) {
      return false;
    }
    TrackPositionResponse rhs = ((TrackPositionResponse) other);
    return new EqualsBuilder().append(position, rhs.position)
        .append(duration, rhs.duration)
        .isEquals();
  }
}
