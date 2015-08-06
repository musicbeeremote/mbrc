package com.kelsos.mbrc.events.ui;

public class DiscoveryStopped {
  private DiscoveryStop reason;

  public DiscoveryStopped(DiscoveryStop reason) {
    this.reason = reason;
  }

  public DiscoveryStop getReason() {
    return reason;
  }

  public enum DiscoveryStop {
    NO_WIFI,
    NOT_FOUND,
    COMPLETE
  }
}
