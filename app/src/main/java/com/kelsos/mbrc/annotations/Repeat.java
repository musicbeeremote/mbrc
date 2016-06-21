package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Repeat {

  public static final String ALL = "all";
  public static final String NONE = "none";
  public static final String ONE = "one";

  private Repeat() {
    //no instance
  }

  @StringDef({ALL, NONE, ONE})
  @Retention(RetentionPolicy.SOURCE)
  public @interface Mode {

  }

}
