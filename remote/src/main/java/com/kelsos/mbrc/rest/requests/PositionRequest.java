package com.kelsos.mbrc.rest.requests;

public class PositionRequest {
  private final int position;

  public PositionRequest(int position) {
    this.position = position;
  }

  public int getPosition() {
    return position;
  }
}
