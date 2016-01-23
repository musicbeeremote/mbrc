package com.kelsos.mbrc.events.ui;

import android.support.annotation.IntRange;

public class VolumeChangeEvent {
  @IntRange(from = -1, to = 100) private int volume;

  private VolumeChangeEvent(@IntRange(from = -1, to = 100) int volume) {
    this.volume = volume;
  }

  public static VolumeChangeEvent newInstance(@IntRange(from = -1, to = 100) int volume) {
    return new VolumeChangeEvent(volume);
  }

  @IntRange(from = -1, to = 100) public int getVolume() {
    return volume;
  }
}
