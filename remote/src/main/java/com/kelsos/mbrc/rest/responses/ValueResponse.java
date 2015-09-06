package com.kelsos.mbrc.rest.responses;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "value"
})
public class ValueResponse {

  @JsonProperty("value")
  private int value;

  /**
   *
   * @return
   *     The value
   */
  @JsonProperty("value")
  public int getValue() {
    return value;
  }

  /**
   *
   * @param value
   *     The value
   */
  @JsonProperty("value")
  public void setValue(int value) {
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
    if (!(other instanceof ValueResponse)) {
      return false;
    }
    ValueResponse rhs = ((ValueResponse) other);
    return new EqualsBuilder().append(value, rhs.value).isEquals();
  }

}
