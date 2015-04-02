package com.kelsos.mbrc.events.ui;

public class DiscoveryStatus {
  private Status reason;

  public DiscoveryStatus(Status reason) {
    this.reason = reason;
  }

  public Status getReason() {
    return reason;
  }

  public enum Status {
    NO_WIFI,
    NOT_FOUND,
    COMPLETE
  }
}
