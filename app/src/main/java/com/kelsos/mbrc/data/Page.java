package com.kelsos.mbrc.data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "total",
    "offset",
    "limit",
    "data"
})
public class Page<T> {

  @JsonProperty("total")
  private int total;
  @JsonProperty("offset")
  private int offset;
  @JsonProperty("limit")
  private int limit;
  @JsonProperty("data")
  private List<T> data = new ArrayList<>();

  @JsonProperty("total")
  public int getTotal() {
    return total;
  }

  @JsonProperty("total")
  public void setTotal(int total) {
    this.total = total;
  }

  @JsonProperty("offset")
  public int getOffset() {
    return offset;
  }

  @JsonProperty("offset")
  public void setOffset(int offset) {
    this.offset = offset;
  }

  @JsonProperty("limit")
  public int getLimit() {
    return limit;
  }

  @JsonProperty("limit")
  public void setLimit(int limit) {
    this.limit = limit;
  }

  @JsonProperty("data")
  public List<T> getData() {
    return data;
  }

  @JsonProperty("data")
  public void setData(List<T> data) {
    this.data = data;
  }
}
