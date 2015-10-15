package com.kelsos.mbrc.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "enabled"
})
public class ChangeStateRequest {

  @JsonProperty("enabled")
  private Boolean enabled;

  /**
   *
   * @return
   * The enabled
   */
  @JsonProperty("enabled")
  public Boolean isEnabled() {
    return enabled;
  }

  /**
   *
   * @param enabled
   * The enabled
   */
  @JsonProperty("enabled")
  public ChangeStateRequest setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(enabled).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof ChangeStateRequest)) {
      return false;
    }
    ChangeStateRequest rhs = ((ChangeStateRequest) other);
    return new EqualsBuilder().append(enabled, rhs.enabled).isEquals();
  }

}
