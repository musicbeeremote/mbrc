package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.data.ConnectionSettings;
import java.util.ArrayList;

public class ConnectionSettingsChanged {
  private ArrayList<ConnectionSettings> mSettings;
  private int defaultIndex;

  public ConnectionSettingsChanged(ArrayList<ConnectionSettings> mSettings, int defaultIndex) {
    this.mSettings = mSettings;
    this.defaultIndex = defaultIndex;
  }

  public ArrayList<ConnectionSettings> getSettings() {
    return mSettings;
  }

  public int getDefaultIndex() {
    return defaultIndex;
  }
}
