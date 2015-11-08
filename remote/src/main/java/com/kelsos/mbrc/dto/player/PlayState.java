package com.kelsos.mbrc.dto.player;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.dto.BaseResponse;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "value"
})
public class PlayState extends BaseResponse {

  @JsonProperty("value")
  @PlayerState.State
  private String value;

  /**
   * @return The value
   */
  @JsonProperty("value")
  @PlayerState.State
  public String getValue() {
    return value;
  }

  /**
   * @param value The value
   */
  @JsonProperty("value")
  public void setValue(@PlayerState.State String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(value).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PlayState)) {
      return false;
    }
    PlayState rhs = ((PlayState) other);
    return new EqualsBuilder().append(value, rhs.value).isEquals();
  }

}
