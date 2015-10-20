package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.dto.track.TrackInfo;

public class TrackInfoChangeEvent {

  private final TrackInfo trackInfo;

  public TrackInfoChangeEvent(TrackInfo trackInfo) {
    this.trackInfo = trackInfo;
  }

  public static TrackInfoChangeEvent newInstance(TrackInfo trackInfo) {
    return new TrackInfoChangeEvent(trackInfo);
  }

  public TrackInfo getTrackInfo() {
    return trackInfo;
  }
}
