package com.kelsos.mbrc.dto.player;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.dto.BaseResponse;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "state"
})
public class Shuffle extends BaseResponse {

  @JsonProperty("state")
  @com.kelsos.mbrc.annotations.Shuffle.State
  private String state;

  /**
   *
   * @return
   *     The state
   */
  @JsonProperty("state")
  @com.kelsos.mbrc.annotations.Shuffle.State
  public String getState() {
    return state;
  }

  /**
   *
   * @param state
   *     The state
   */
  @JsonProperty("state")
  public void setState(@com.kelsos.mbrc.annotations.Shuffle.State String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(state).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Shuffle)) {
      return false;
    }
    Shuffle rhs = ((Shuffle) other);
    return new EqualsBuilder().append(state, rhs.state).isEquals();
  }

}
