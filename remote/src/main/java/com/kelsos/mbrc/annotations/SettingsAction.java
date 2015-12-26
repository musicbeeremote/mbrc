package com.kelsos.mbrc.annotations;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SettingsAction {
  public static final int DELETE = 1;
  public static final int EDIT = 2;
  public static final int DEFAULT = 3;
  public static final int NEW = 4;

  private SettingsAction() {
    //no instance
  }

  @IntDef({
      DELETE,
      EDIT,
      DEFAULT,
      NEW

  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface Action {

  }
}
