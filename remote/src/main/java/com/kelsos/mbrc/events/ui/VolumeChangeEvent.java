package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.dto.player.Volume;

public class VolumeChangeEvent {
  private Volume volume;

  private VolumeChangeEvent(Volume volume) {
    this.volume = volume;
  }

  public static VolumeChangeEvent newInstance(Volume volume) {
    return new VolumeChangeEvent(volume);
  }

  public Volume getVolume() {
    return volume;
  }
}
