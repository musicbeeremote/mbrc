package com.kelsos.mbrc.events.ui;

public class ScrobbleChange {
  private boolean isActive;

  public ScrobbleChange(boolean isActive) {
    this.isActive = isActive;
  }

  public boolean getIsActive() {
    return this.isActive;
  }
}
