package com.kelsos.mbrc.events;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ChangeWebSocketStatusEvent {
  public static final int CONNECT = 1;
  public static final int DISCONNECT = 2;

  @Action private int action;

  private ChangeWebSocketStatusEvent() {
    //no instance
  }

  public static ChangeWebSocketStatusEvent newInstance(@Action int action) {
    ChangeWebSocketStatusEvent event = new ChangeWebSocketStatusEvent();
    event.action = action;
    return event;
  }

  @Action public int getAction() {
    return action;
  }

  @IntDef({
      CONNECT,
      DISCONNECT
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Action {

  }
}
