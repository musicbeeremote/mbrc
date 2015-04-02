package com.kelsos.mbrc.rest.responses;

public class StateResponse {
  private boolean enabled;

  public StateResponse(boolean enabled) {
    this.enabled = enabled;
  }

  public StateResponse() { }

  public boolean isEnabled() {
    return enabled;
  }
}
