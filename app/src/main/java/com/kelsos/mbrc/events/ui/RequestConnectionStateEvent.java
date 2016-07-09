package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;

public class RequestConnectionStateEvent {
  private RequestConnectionStateEvent() {
    //no instance
  }

  @NonNull
  public static RequestConnectionStateEvent create() {
    return new RequestConnectionStateEvent();
  }
}
