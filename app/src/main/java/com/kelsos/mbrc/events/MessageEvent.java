package com.kelsos.mbrc.events;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.node.TextNode;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.interfaces.IEvent;

public class MessageEvent implements IEvent {
  private String type;
  private Object data;

  public MessageEvent(String type) {
    this.type = type;
    data = "";
  }

  public MessageEvent(String type, Object data) {
    this.type = type;
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public Object getData() {
    return data;
  }

  public String getDataString() {
    String result = null;
    if (data.getClass() == TextNode.class) {
      result = ((TextNode) data).asText();
    } else if (data.getClass() == String.class) {
      result = (String) data;
    }
    return result;
  }

  @NonNull
  public static MessageEvent action(Object data) {
    return new MessageEvent(ProtocolEventType.UserAction, data);
  }
}
