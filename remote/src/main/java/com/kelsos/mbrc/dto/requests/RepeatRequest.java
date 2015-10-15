package com.kelsos.mbrc.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.annotations.RepeatMode;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mode"
})
public class RepeatRequest {

  @RepeatMode
  @JsonProperty("mode")
  private String mode;

  /**
   *
   * @return
   * The mode
   */
  @RepeatMode
  @JsonProperty("mode")
  public String getMode() {
    return mode;
  }

  /**
   *
   * @param mode
   * The mode
   */
  @JsonProperty("mode")
  public RepeatRequest setMode(@RepeatMode String mode) {
    this.mode = mode;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(mode).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof RepeatRequest)) {
      return false;
    }
    RepeatRequest rhs = ((RepeatRequest) other);
    return new EqualsBuilder().append(mode, rhs.mode).isEquals();
  }
}
