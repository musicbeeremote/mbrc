package com.kelsos.mbrc.data;

public class UserAction {
  private String context;
  private Object data;

  public UserAction(String context, Object data) {
    this.context = context;
    this.data = data;
  }

  public String getContext() {
    return context;
  }

  public Object getData() {
    return data;
  }
}
