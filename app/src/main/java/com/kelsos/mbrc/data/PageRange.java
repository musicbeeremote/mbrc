package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "offset",
    "limit",
}) public class PageRange {

  @JsonProperty("offset") private int offset;
  @JsonProperty("limit") private int limit;

  @JsonProperty("limit") public int getLimit() {
    return limit;
  }

  @JsonProperty("limit") public void setLimit(int limit) {
    this.limit = limit;
  }

  @JsonProperty("offset")public int getOffset() {
    return offset;
  }

  @JsonProperty("offset")public void setOffset(int offset) {
    this.offset = offset;
  }
}
