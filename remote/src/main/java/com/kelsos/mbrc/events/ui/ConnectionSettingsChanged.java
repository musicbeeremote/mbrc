package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ConnectionSettings;
import java.util.List;

public class ConnectionSettingsChanged {
  private List<ConnectionSettings> settings;
  private int defaultIndex;

  public ConnectionSettingsChanged(List<ConnectionSettings> settings, int defaultIndex) {
    this.settings = settings;
    this.defaultIndex = defaultIndex;
  }

  public List<ConnectionSettings> getSettings() {
    return settings;
  }

  public int getDefaultIndex() {
    return defaultIndex;
  }
}
