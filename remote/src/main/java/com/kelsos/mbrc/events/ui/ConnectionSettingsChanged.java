package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.List;

public class ConnectionSettingsChanged {
    private List<ConnectionSettings> mSettings;
    private int defaultIndex;

    public ConnectionSettingsChanged(List<ConnectionSettings> mSettings, int defaultIndex) {
        this.mSettings = mSettings;
        this.defaultIndex = defaultIndex;
    }

    public List<ConnectionSettings> getmSettings() {
        return mSettings;
    }

    public int getDefaultIndex() {
        return defaultIndex;
    }
}
