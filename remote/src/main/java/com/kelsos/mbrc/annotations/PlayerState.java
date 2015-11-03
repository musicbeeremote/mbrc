package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PlayerState {
  public static final String PLAYING = "playing";
  public static final String PAUSED = "paused";
  public static final String STOPPED = "stopped";

  private PlayerState() {
    //no instance
  }

  @Retention(RetentionPolicy.SOURCE)
  @StringDef({
                 PAUSED,
                 PLAYING,
                 STOPPED
             })
  public @interface State {
  }
}
