
package com.kelsos.mbrc.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.annotations.ShuffleState;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status"
})
public class ShuffleRequest {

  @JsonProperty("status")
  @ShuffleState
  private String status;

  /**
   *
   * @return
   * The status
   */
  @JsonProperty("status")
  @ShuffleState
  public String getStatus() {
    return status;
  }

  /**
   *
   * @param status
   * The status
   */
  @JsonProperty("status")
  public ShuffleRequest setStatus(@ShuffleState String status) {
    this.status = status;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }


  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(status).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof ShuffleRequest)) {
      return false;
    }
    ShuffleRequest rhs = ((ShuffleRequest) other);
    return new EqualsBuilder().append(status, rhs.status).isEquals();
  }

}
