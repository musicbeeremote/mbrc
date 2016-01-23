package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Shuffle {
  public static final String OFF = "off";
  public static final String AUTODJ = "autodj";
  public static final String ON = "on";
  public static final String TOGGLE = "";
  public static final String UNDEF = "undef";

  private Shuffle() {
    //no instance
  }

  @StringDef({
      OFF,
      AUTODJ,
      ON,
      TOGGLE,
      UNDEF
  })

  @Retention(RetentionPolicy.SOURCE)
  public @interface State {
  }
}
