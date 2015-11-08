package com.kelsos.mbrc.annotations;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LastfmState {
  public static final String LOVE = "Love";
  public static final String BAN = "Ban";
  public static final String NORMAL = "Normal";
  private LastfmState() {
    //no instance
  }

  @Retention(RetentionPolicy.SOURCE)
  @StringDef({
                 BAN,
                 LOVE,
                 NORMAL
             })
  public @interface State {
  }
}
