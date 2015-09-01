package com.kelsos.mbrc.rest.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "path"
}) public class PlayPathRequest {

  @JsonProperty("path") private String path;

  /**
   * @return The path
   */
  @JsonProperty("path") public String getPath() {
    return path;
  }

  /**
   * @param path The path
   */
  @JsonProperty("path") public PlayPathRequest setPath(String path) {
    this.path = path;
    return this;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(path).toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PlayPathRequest)) {
      return false;
    }
    PlayPathRequest rhs = ((PlayPathRequest) other);
    return new EqualsBuilder().append(path, rhs.path).isEquals();
  }
}
