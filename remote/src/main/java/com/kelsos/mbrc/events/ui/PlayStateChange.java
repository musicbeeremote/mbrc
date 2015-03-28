package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.PlayState;

public class PlayStateChange {
  private PlayState state;

  public PlayStateChange(PlayState state) {
    this.state = state;
  }

  public PlayState getState() {
    return this.state;
  }
}
