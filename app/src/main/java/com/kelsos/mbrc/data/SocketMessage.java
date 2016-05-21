package com.kelsos.mbrc.data;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SocketMessage {
  @JsonProperty private String context;

  @JsonProperty private Object data;

  private SocketMessage(String context, Object data) {
    this.context = context;
    this.data = data;
  }

  public SocketMessage() {

  }

  @NonNull public static SocketMessage create(String context, Object data) {
    return new SocketMessage(context, data);
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
