package com.kelsos.mbrc.rest.responses;

public class TrackPositionResponse {
  private int position;
  private int duration;

  public TrackPositionResponse(int position, int duration) {
    this.position = position;
    this.duration = duration;
  }

  public TrackPositionResponse() { }

  public int getDuration() {
    return duration;
  }

  public int getPosition() {
    return position;
  }
}
