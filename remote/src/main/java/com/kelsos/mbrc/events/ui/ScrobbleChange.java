package com.kelsos.mbrc.events.ui;

public class ScrobbleChange {
  private boolean active;

  public ScrobbleChange(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return this.active;
  }
}
