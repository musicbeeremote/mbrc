package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlayerAction {
  public static final String STOP = "stop";
  public static final String PLAY = "play";
  public static final String PAUSE = "pause";
  public static final String NEXT = "next";
  public static final String PREVIOUS = "previous";
  public static final String PLAY_PLAUSE = "playpause";

  private PlayerAction() {
    //no instance
  }

  @Retention(RetentionPolicy.SOURCE)
  @StringDef({
                 NEXT,
                 STOP,
                 PLAY,
                 PAUSE,
                 PREVIOUS,
                 PLAY_PLAUSE
             })
  public @interface Action {
  }
}
