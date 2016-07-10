package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;

public class ConnectionSettingsChanged {
  private long defaultId;

  private ConnectionSettingsChanged(long defaultId) {
    this.defaultId = defaultId;
  }

  @NonNull
  public static ConnectionSettingsChanged newInstance(long defaultId) {
    return new ConnectionSettingsChanged(defaultId);
  }

  public long getDefaultId() {
    return defaultId;
  }
}
