package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Repeat {
  public static final String ALL = "all";
  public static final String NONE = "none";
  public static final String ONE = "one";
  public static final String CHANGE = "";

  private Repeat() {
    //no instance
  }

  @StringDef({
                 ALL,
                 NONE,
                 ONE,
                 CHANGE
             })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Mode {
  }
}
