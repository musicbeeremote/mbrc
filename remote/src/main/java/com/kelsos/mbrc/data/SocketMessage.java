package com.kelsos.mbrc.data;

import org.codehaus.jackson.annotate.JsonProperty;

public class SocketMessage {
  @JsonProperty private String context;
  @JsonProperty private Object data;

  public SocketMessage(String context, Object data) {
    this.context = context;
    this.data = data;
      }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
