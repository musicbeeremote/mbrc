package com.kelsos.mbrc.events.ui;

public class ConnectionStatusChange {
  private Status status;

  public ConnectionStatusChange(Status status) {
    this.status = status;
  }

  public Status getStatus() {
    return this.status;
  }

  public enum Status {
    CONNECTION_OFF,
    CONNECTION_ACTIVE
  }
}
