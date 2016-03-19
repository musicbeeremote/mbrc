package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.annotations.SettingsAction;

public class ChangeSettings {
  private int action;
  private DeviceSettings settings;

  public ChangeSettings(@SettingsAction.Action int action, DeviceSettings settings) {
    this.action = action;
    this.settings = settings;
  }

  @SettingsAction.Action public int getAction() {
    return action;
  }

  public DeviceSettings getSettings() {
    return settings;
  }
}
