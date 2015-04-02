package com.kelsos.mbrc.rest.responses;

@SuppressWarnings("UnusedDeclaration") public class SuccessStateResponse extends SuccessResponse {
  private boolean enabled;

  public SuccessStateResponse(boolean success, boolean enabled) {
    super(success);
    this.enabled = enabled;
  }

  public SuccessStateResponse() {
    super();
  }

  public boolean isEnabled() {
    return enabled;
  }
}
