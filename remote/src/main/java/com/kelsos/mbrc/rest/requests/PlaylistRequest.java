package com.kelsos.mbrc.rest.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "name",
    "list"
})

public class PlaylistRequest {

  @JsonProperty("name") private String name;
  @JsonProperty("list") private List<String> list = new ArrayList<>();

  /**
   * @return The name
   */
  @JsonProperty("name") public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  @JsonProperty("name") public PlaylistRequest setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return The list
   */
  @JsonProperty("list") public List<String> getList() {
    return list;
  }

  /**
   * @param list The list
   */
  @JsonProperty("list") public PlaylistRequest setList(List<String> list) {
    this.list = list;
    return this;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(name)
        .append(list)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PlaylistRequest)) {
      return false;
    }
    PlaylistRequest rhs = ((PlaylistRequest) other);
    return new EqualsBuilder().append(name, rhs.name)
        .append(list, rhs.list)
        .isEquals();
  }
}
