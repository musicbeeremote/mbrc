package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.DiscoveryStop;

public class DiscoveryStopped {
  private DiscoveryStop reason;

  public DiscoveryStopped(DiscoveryStop reason) {
    this.reason = reason;
  }

  public DiscoveryStop getReason() {
    return reason;
  }
}
