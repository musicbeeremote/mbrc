package com.kelsos.mbrc.events.ui;

public class VolumeChange {
  private int volume;
  private boolean mute;

  public VolumeChange(int vol) {
    this.volume = vol;
    this.mute = false;
  }

  public VolumeChange() {
    this.volume = 0;
    this.mute = true;
  }

  public int getVolume() {
    return this.volume;
  }

  public boolean isMute() {
    return this.mute;
  }
}
