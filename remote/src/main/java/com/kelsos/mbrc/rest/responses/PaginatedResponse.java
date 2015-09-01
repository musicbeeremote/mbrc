package com.kelsos.mbrc.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "data",
    "total",
    "limit",
    "offset"
})

public class PaginatedResponse<T> {

  @JsonProperty("data") private List<T> data = new ArrayList<T>();
  @JsonProperty("total") private int total;
  @JsonProperty("limit") private int limit;
  @JsonProperty("offset") private int offset;

  /**
   * @return The data
   */
  @JsonProperty("data") public List<T> getData() {
    return data;
  }

  /**
   * @param data The data
   */
  @JsonProperty("data") public PaginatedResponse<T> setData(List<T> data) {
    this.data = data;
    return this;
  }

  /**
   * @return The total
   */
  @JsonProperty("total") public int getTotal() {
    return total;
  }

  /**
   * @param total The total
   */
  @JsonProperty("total") public PaginatedResponse<T> setTotal(int total) {
    this.total = total;
    return this;
  }

  /**
   * @return The limit
   */
  @JsonProperty("limit") public int getLimit() {
    return limit;
  }

  /**
   * @param limit The limit
   */
  @JsonProperty("limit") public PaginatedResponse<T> setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * @return The offset
   */
  @JsonProperty("offset") public int getOffset() {
    return offset;
  }

  /**
   * @param offset The offset
   */
  @JsonProperty("offset") public PaginatedResponse<T> setOffset(int offset) {
    this.offset = offset;
    return this;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(data)
        .append(total)
        .append(limit)
        .append(offset)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PaginatedResponse)) {
      return false;
    }
    PaginatedResponse rhs = ((PaginatedResponse) other);
    return new EqualsBuilder().append(data, rhs.data)
        .append(total, rhs.total)
        .append(limit, rhs.limit)
        .append(offset, rhs.offset)
        .isEquals();
  }
}
