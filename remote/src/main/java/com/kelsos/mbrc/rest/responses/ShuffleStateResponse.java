package com.kelsos.mbrc.rest.responses;

import com.kelsos.mbrc.annotations.ShuffleState;

public class ShuffleStateResponse {
  private String state;

  public ShuffleStateResponse(@ShuffleState String state) {
    this.state = state;
  }

  @ShuffleState
  public String getState() {
    return state;
  }
}
