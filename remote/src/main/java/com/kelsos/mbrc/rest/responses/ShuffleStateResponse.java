package com.kelsos.mbrc.rest.responses;

import com.kelsos.mbrc.events.ui.ShuffleChange;

public class ShuffleStateResponse {
  private String state;

  public ShuffleStateResponse(@ShuffleChange.ShuffleState String state) {
    this.state = state;
  }

  @ShuffleChange.ShuffleState
  public String getState() {
    return state;
  }
}
