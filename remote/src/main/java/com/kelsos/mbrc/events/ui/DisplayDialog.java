package com.kelsos.mbrc.events.ui;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DisplayDialog {
  public static final int NONE = 0;
  public static final int SETUP = 1;
  public static final int UPGRADE = 2;
  public static final int INSTALL = 3;

  @IntDef({NONE, SETUP, UPGRADE, INSTALL})
  @Retention(RetentionPolicy.SOURCE)
  public @interface DialogType { }

  private int dialogType;

  public DisplayDialog(@DialogType int dialogType) {
    this.dialogType = dialogType;
  }

  @DialogType
  public int getDialogType() {
    return dialogType;
  }
}
