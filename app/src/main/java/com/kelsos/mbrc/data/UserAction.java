package com.kelsos.mbrc.data;

import android.support.annotation.NonNull;

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

  @NonNull public static UserAction create(String context) {
    return new UserAction(context, true);
  }
}
