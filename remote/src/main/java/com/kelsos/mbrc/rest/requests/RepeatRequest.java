package com.kelsos.mbrc.rest.requests;

import com.kelsos.mbrc.annotations.RepeatMode;

public class RepeatRequest {
  @RepeatMode private final String mode;

  public RepeatRequest(@RepeatMode String mode) {
    this.mode = mode;
  }

  @RepeatMode
  public String getMode() {
    return mode;
  }
}
