package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kelsos.mbrc.annotations.QueueAction;

public class Queue {
  @JsonProperty private String type;
  @JsonProperty private String query;

  public Queue(@QueueAction String type, String query) {
    this.type = type;
    this.query = query;
  }

  @QueueAction public String getType() {
    return type;
  }

  @SuppressWarnings("unused") public String getQuery() {
    return query;
  }
}
