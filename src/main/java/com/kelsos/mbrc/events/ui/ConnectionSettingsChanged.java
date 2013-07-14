package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ConnectionSettings;

import java.util.ArrayList;

public class ConnectionSettingsChanged {
    private ArrayList<ConnectionSettings> mSettings;

    public ConnectionSettingsChanged(ArrayList<ConnectionSettings> mSettings) {
        this.mSettings = mSettings;
    }

    public ArrayList<ConnectionSettings> getmSettings() {
        return mSettings;
    }
}
