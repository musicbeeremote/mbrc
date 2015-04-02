package com.kelsos.mbrc.rest.responses;

public class PlayerStatusResponse {
  private String repeat;
  private boolean mute;
  private boolean shuffle;
  private boolean scrobble;
  private String state;
  private int volume;

  public PlayerStatusResponse() { }

  public String getRepeat() {
    return repeat;
  }

  public boolean isMute() {
    return mute;
  }

  public boolean isShuffle() {
    return shuffle;
  }

  public boolean isScrobble() {
    return scrobble;
  }

  public String getState() {
    return state;
  }

  public int getVolume() {
    return volume;
  }
}

