package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "code"
})
public class BaseResponse {

  @JsonProperty("code") private int code;

  /**
   * @return The code status of the request
   */
  @JsonProperty("code") public int getCode() {
    return code;
  }

  /**
   * @param code The code status of the request
   */
  @JsonProperty("code") public void setCode(int code) {
    this.code = code;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(code)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof BaseResponse)) {
      return false;
    }
    BaseResponse rhs = ((BaseResponse) other);
    return new EqualsBuilder().append(code, rhs.code)
        .isEquals();
  }
}
