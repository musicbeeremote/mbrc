package com.kelsos.mbrc.events;

import com.kelsos.mbrc.interfaces.IEvent;

public class MessageEvent implements IEvent {
  private String type;
  private Object data;
  private String message;

  public MessageEvent(String type) {
    this.type = type;
    data = "";
  }

  public MessageEvent(String type, Object data) {
    this.type = type;
    this.data = data;
    if (data instanceof String) {
      message = (String) data;
    }
  }

  public String getType() {
    return type;
  }

  @Override public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }
}
