package com.kelsos.mbrc.events.ui;

public class VolumeChange {
  private int volume;
  private boolean isMute;

  public VolumeChange(int vol) {
    this.volume = vol;
    this.isMute = false;
  }

  public VolumeChange() {
    this.volume = 0;
    this.isMute = true;
  }

  public int getVolume() {
    return this.volume;
  }

  public boolean getIsMute() {
    return this.isMute;
  }
}
