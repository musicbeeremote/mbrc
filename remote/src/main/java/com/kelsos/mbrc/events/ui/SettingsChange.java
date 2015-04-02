package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.enums.SettingsAction;

public class SettingsChange {

  private SettingsAction action;
  private ConnectionSettings settings;

  public SettingsChange(SettingsAction action, ConnectionSettings settings) {
    this.action = action;
    this.settings = settings;
  }

  public SettingsAction getAction() {
    return action;
  }

  public ConnectionSettings getSettings() {
    return settings;
  }
}
