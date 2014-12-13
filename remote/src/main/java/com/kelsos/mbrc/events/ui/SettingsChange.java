package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.enums.SettingsAction;

public class SettingsChange {
    private int index;
    private SettingsAction action;
    private ConnectionSettings settings;

    public SettingsChange(int index, SettingsAction action) {
        this.index = index;
        this.action = action;
    }

    public SettingsChange(SettingsAction action, ConnectionSettings settings) {
        this.index = -1;
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
