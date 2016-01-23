package com.kelsos.mbrc.annotations;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Mute {
  public static final int UNDEF = -1;
  public static final int OFF = 0;
  public static final int ON = 1;

  @IntDef({
      ON,
      OFF,
      UNDEF
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface State {

  }
}
