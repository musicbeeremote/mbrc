package com.kelsos.mbrc.events.ui;

import org.codehaus.jackson.node.ObjectNode;

public class TrackMoved {
  private boolean success;
  private int from;
  private int to;

  public TrackMoved(ObjectNode node) {
    success = node.path("success").asBoolean();
    from = node.path("from").asInt();
    to = node.path("to").asInt();
  }

  public boolean isSuccess() {
    return success;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }
}
