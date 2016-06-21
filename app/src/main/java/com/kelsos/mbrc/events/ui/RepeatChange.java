package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.annotations.Repeat.Mode;

public class RepeatChange {
  @Mode
  private String mode;

  public RepeatChange(@Mode String mode) {
    this.mode = mode;
  }

  @Mode
  public String getMode() {
    return this.mode;
  }
}
