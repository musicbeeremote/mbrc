package com.kelsos.mbrc.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "success"
})
public class SuccessResponse {

  @JsonProperty("success") private Boolean success;

  /**
   * @return The success status of the request
   */
  @JsonProperty("success") public Boolean getSuccess() {
    return success;
  }

  /**
   * @param success The success status of the request
   */
  @JsonProperty("success") public void setSuccess(Boolean success) {
    this.success = success;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(success)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof SuccessResponse)) {
      return false;
    }
    SuccessResponse rhs = ((SuccessResponse) other);
    return new EqualsBuilder().append(success, rhs.success)
        .isEquals();
  }
}
