package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.enums.SettingsAction;

public class ChangeSettings {
  private SettingsAction action;
  private ConnectionSettings settings;

  public ChangeSettings(SettingsAction action, ConnectionSettings settings) {
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
