package com.kelsos.mbrc.events.ui;

public class RepeatChange {
  private boolean active;

  public RepeatChange(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return this.active;
  }
}
