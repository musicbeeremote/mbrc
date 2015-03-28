package com.kelsos.mbrc.events.ui;

import org.codehaus.jackson.node.ObjectNode;

public class TrackRemoval {
  private int index;
  private boolean success;

  public TrackRemoval(ObjectNode node) {
    index = node.path("index").asInt();
    success = node.path("success").asBoolean();
  }

  public int getIndex() {
    return this.index;
  }

  public boolean isSuccess() {
    return success;
  }
}
