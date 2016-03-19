package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.domain.DeviceSettings;
import java.util.List;

public class ConnectionSettingsChanged {
  private List<DeviceSettings> settings;
  private int defaultIndex;

  public ConnectionSettingsChanged(List<DeviceSettings> settings, int defaultIndex) {
    this.settings = settings;
    this.defaultIndex = defaultIndex;
  }

  public List<DeviceSettings> getSettings() {
    return settings;
  }

  public int getDefaultIndex() {
    return defaultIndex;
  }
}
