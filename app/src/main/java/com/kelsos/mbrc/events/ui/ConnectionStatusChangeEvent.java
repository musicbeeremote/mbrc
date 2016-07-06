package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.annotations.Connection.Status;

public class ConnectionStatusChangeEvent {
  @Status
  private int status;

  public ConnectionStatusChangeEvent(@Status int status) {
    this.status = status;
  }

  @Status
  public int getStatus() {
    return this.status;
  }
}
