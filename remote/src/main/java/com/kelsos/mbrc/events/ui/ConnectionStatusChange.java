package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.ConnectionStatus;
public class ConnectionStatusChange {
  private ConnectionStatus status;

  public ConnectionStatusChange(ConnectionStatus status) {
    this.status = status;
  }

  public ConnectionStatus getStatus() {
    return this.status;
  }
}
