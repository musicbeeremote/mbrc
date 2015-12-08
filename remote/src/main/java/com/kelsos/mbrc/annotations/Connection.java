package com.kelsos.mbrc.annotations;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Connection {
  public static final int OFF = 0;
  public static final int ON = 1;
  private Connection() {
    //no instance
  }

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
      OFF,
      ON
  })
  public @interface Status {

  }
}
