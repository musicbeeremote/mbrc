package com.kelsos.mbrc.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "enabled",
    "success"
})
public class SuccessBooleanStateResponse {

  @JsonProperty("enabled")
  private Boolean enabled;
  @JsonProperty("success")
  private Boolean success;

  /**
   *
   * @return
   *     The enabled
   */
  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }

  /**
   *
   * @param enabled
   *     The enabled
   */
  @JsonProperty("enabled")
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  /**
   *
   * @return
   *     The success
   */
  @JsonProperty("success")
  public Boolean getSuccess() {
    return success;
  }

  /**
   *
   * @param success
   *     The success
   */
  @JsonProperty("success")
  public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(enabled).append(success).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof SuccessBooleanStateResponse)) {
      return false;
    }
    SuccessBooleanStateResponse rhs = ((SuccessBooleanStateResponse) other);
    return new EqualsBuilder().append(enabled, rhs.enabled).append(success, rhs.success).isEquals();
  }

}

