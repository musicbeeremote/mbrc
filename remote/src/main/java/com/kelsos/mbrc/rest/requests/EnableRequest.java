package com.kelsos.mbrc.rest.requests;

public class EnableRequest {
  private final Boolean enabled;

  public EnableRequest(Boolean enabled) {
    this.enabled = enabled;
  }

  public Boolean isEnabled() {
    return enabled;
  }
}
