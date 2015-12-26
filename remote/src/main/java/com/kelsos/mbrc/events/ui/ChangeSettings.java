package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.annotations.SettingsAction;

public class ChangeSettings {
  private int action;
  private ConnectionSettings settings;

  public ChangeSettings(@SettingsAction.Action int action, ConnectionSettings settings) {
    this.action = action;
    this.settings = settings;
  }

  @SettingsAction.Action public int getAction() {
    return action;
  }

  public ConnectionSettings getSettings() {
    return settings;
  }
}
