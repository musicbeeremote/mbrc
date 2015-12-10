package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Queue {
  public static final String NEXT = "next";
  public static final String LAST = "last";
  public static final String NOW = "now";

  private Queue() {
    //no instance
  }

  @StringDef({
      NEXT,
      LAST,
      NOW
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Action {
  }
}
