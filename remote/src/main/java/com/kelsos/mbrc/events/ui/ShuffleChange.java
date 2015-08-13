package com.kelsos.mbrc.events.ui;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ShuffleChange {

  public static final String OFF = "off";
  public static final String AUTODJ = "autodj";
  public static final String SHUFFLE = "shuffle";
  private String shuffleState;

  public ShuffleChange(@ShuffleState String shuffleState) {
    this.shuffleState = shuffleState;
  }

  @ShuffleState public String getShuffleState() {
    return this.shuffleState;
  }

  @StringDef({
      OFF,
      AUTODJ,
      SHUFFLE
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface ShuffleState { }
}
