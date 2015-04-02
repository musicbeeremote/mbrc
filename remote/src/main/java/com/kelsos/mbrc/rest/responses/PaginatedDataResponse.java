package com.kelsos.mbrc.rest.responses;

import java.util.List;
import org.codehaus.jackson.JsonNode;

public class PaginatedDataResponse {
  private int offset;
  private int limit;
  private int total;
  private List<JsonNode> data;

  public PaginatedDataResponse(int offset, int limit, int total, List<JsonNode> data) {
    this.offset = offset;
    this.limit = limit;
    this.total = total;
    this.data = data;
  }

  private PaginatedDataResponse() { }

  public List<JsonNode> getData() {
    return data;
  }

  public int getTotal() {
    return total;
  }

  public int getLimit() {
    return limit;
  }

  public int getOffset() {
    return offset;
  }
}
