package com.kelsos.mbrc.events;

import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.interfaces.IEvent;

public class MessageEvent implements IEvent {
  @UserInputEventType.Event private String type;

  private MessageEvent(@UserInputEventType.Event String type) {
    this.type = type;
  }

  public static MessageEvent newInstance(@UserInputEventType.Event String type) {
    return new MessageEvent(type);
  }

  @UserInputEventType.Event public String getType() {
    return type;
  }
}
