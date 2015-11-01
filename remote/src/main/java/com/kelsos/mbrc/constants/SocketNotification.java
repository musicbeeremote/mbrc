package com.kelsos.mbrc.constants;

public class SocketNotification {
  public static final String MUTE = "mute-status-changed";
  public static final String VOLUME = "volume-changed";
  public static final String NOW_PLAYING = "nowplaying-list-changed";
  public static final String PLAY_STATUS = "play-status-changed";
  public static final String COVER = "cover-changed";
  public static final String LYRICS = "lyrics-changed";
  public static final String TRACK = "track-changed";
  public static final String REPEAT = "repeat-status-changed";

  private SocketNotification() {
    //no instance
  }
}
