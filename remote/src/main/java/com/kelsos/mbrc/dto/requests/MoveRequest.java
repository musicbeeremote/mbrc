package com.kelsos.mbrc.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "from",
    "to"
})

public class MoveRequest {

  @JsonProperty("from")
  private int from;
  @JsonProperty("to")
  private int to;

  /**
   *
   * @return
   * The from
   */
  @JsonProperty("from")
  public int getFrom() {
    return from;
  }

  /**
   *
   * @param from
   * The from
   */
  @JsonProperty("from")
  public MoveRequest setFrom(int from) {
    this.from = from;
    return this;
  }

  /**
   *
   * @return
   * The to
   */
  @JsonProperty("to")
  public int getTo() {
    return to;
  }

  /**
   *
   * @param to
   * The to
   */
  @JsonProperty("to")
  public MoveRequest setTo(int to) {
    this.to = to;
    return this;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }


  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(from).append(to).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof MoveRequest)) {
      return false;
    }
    MoveRequest rhs = ((MoveRequest) other);
    return new EqualsBuilder().append(from, rhs.from).append(to, rhs.to).isEquals();
  }

}
