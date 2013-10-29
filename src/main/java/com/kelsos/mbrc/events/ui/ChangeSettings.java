package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.model.ConnectionSettings;
import com.kelsos.mbrc.enums.SettingsAction;

public class ChangeSettings {
    private int index;
    private SettingsAction action;
    private ConnectionSettings settings;

    public ChangeSettings(int index, SettingsAction action) {
        this.index = index;
        this.action = action;
    }

    public ChangeSettings(int index, SettingsAction action, ConnectionSettings settings) {
        this.index = index;
        this.action = action;
        this.settings = settings;
    }

    public int getIndex() {
        return index;
    }

    public SettingsAction getAction() {
        return action;
    }

    public ConnectionSettings getSettings() {
        return settings;
    }
}
